package com.my.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

@Component
public class SensitiveFilter {

    private static final Logger log = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换符
    private static final String REPLACEMENT = "***";
    //根节点
    TrieNode rootNode = new TrieNode();

    // @PostConstruct 表示当 bean 被初始化之后执行改方法
    @PostConstruct
    private void init() {
        try (
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String keyWord;
            while ((keyWord = bufferedReader.readLine()) != null) {
                //添加到前缀树
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            log.error("加载敏感词文件失败:" + e.getMessage());
        }
    }

    // 将文件中的字符 添加到 TrieNode 数据结构中
    private void addKeyWord(String keyWord) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyWord.length(); i++) {
            char charAt = keyWord.charAt(i);
            //先获取子节点,看该节点是否已经存在
            TrieNode subNode = tempNode.getSubNodes(charAt);
            //如果子节点不存在
            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                //将该子节点挂到父节点下
                tempNode.addSubNodes(charAt, subNode);
            }
            //子节点已经存在,指向子节点,进入下一轮循环
            tempNode = subNode;
            //当遍历到最后一位时,更改关键词的结束标识为 true
            if (i == keyWord.length() - 1) {
                tempNode.setKeyWordsEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //  指针1
        TrieNode tempNode = rootNode;
        //  指针2
        int begin = 0;
        //  指针3
        int position = 0;
        //  结果
        StringBuilder sb = new StringBuilder();
        while (begin < text.length() - 1) {
            char charAt = text.charAt(position);
            //跳过符号
            if (isSymbol(charAt)) {
                // 如果指针处于根节点,则将此符号计入结果,让指针2指向下一个元素
                if (tempNode == rootNode) {
                    sb.append(charAt);
                    begin++;
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNodes(charAt);
            if (tempNode == null) {
                // 以 begin 开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeyWordsEnd()) {
                //发现敏感词,将 begin~position 字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            } else {
                //检查下一个字符
                if (position < text.length() - 1) {
                    position++;
                }
            }
        }
        //将最后一批字符计入结果
        return sb.toString();
    }

    private boolean isSymbol(Character character) {
        //  0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(character) && (character < 0x2E80 || character > 0x9FFF);
    }

    //前缀树
    private class TrieNode {

        //关键词结束的标识
        boolean isKeyWordsEnd = false;

        //子节点(key是下级节点字符,value是下级节点)
        HashMap<Character, TrieNode> subNodes = new HashMap<Character, TrieNode>();

        public boolean isKeyWordsEnd() {
            return isKeyWordsEnd;
        }

        public void setKeyWordsEnd(boolean keyWordsEnd) {
            isKeyWordsEnd = keyWordsEnd;
        }

        //增加子节点
        public void addSubNodes(Character character, TrieNode node) {
            subNodes.put(character, node);
        }

        //获取子节点
        public TrieNode getSubNodes(Character character) {
            return subNodes.get(character);
        }
    }
}
