$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");
    let title = $("#recipient-name").val();
    let content = $("#message-text").val();
    //发送异步ajax请求
    $.post(
        CONTEXT_PATH + "/discussPost/add",
        {"title": title, "content": content},
        function (data) {
            data = $.parseJSON(data);
            //在提示框中显示服务器返回的Json消息
            $("hintModal").text(data.msg);
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                //刷新页面
                if (data.code == 0){
                    window.location.reload();
                }
            }, 2000);
        }
    );

}