/**
 * Created by liangsongying on 2018/3/2.
 */

$(".d1 .btn").click(function () {
    var receive= $(".d1 .receive").val();
    var content = $(".d1 .content").val();
    $.ajax({
        url:'../email/sendEmail',
        method:'post',
        dataType:'json',
        data:{
            content:content,
            receive:receive
        },
        success:function () {

        },
        error:function () {

        }

    });
});
$(".d2 .btn2").click(function () {
    var receive= $(".d2 .receive").val();
    var content = $(".d2 .content").val();
    $.ajax({
        url:'../email/sendThymeleafEmail',
        method:'post',
        dataType:'json',
        data:{
            content:content,
            receive:receive
        },
        success:function () {

        },
        error:function () {

        }

    });
});
