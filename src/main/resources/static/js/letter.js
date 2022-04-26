$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");
    let toName = $("#recipient-name").val();
    let content = $("#message-text").val();
    $.post(
        CONTEXT_PATH + "/message/letter/send",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#hintBody").text("发送成功!");
            }else if (data.code == 1){
                $("#hintBody").text($("#recipient-name").val()+data.msg);
            }
            else {
                $("#hintBody").text("发送失败!");
            }
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    );
}

function delete_msg() {
    // TODO 删除数据
    $(this).parents(".media").remove();
}