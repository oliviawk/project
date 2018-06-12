// function baseSourceClick(id) {
// 	$(id).click(function() {
// 		var ip;
// 		if (id == "#collect") {
// 			ip = "10.30.16.220";
// 		}
// 		directorUsage("../fzjc/getDirectoryUsedData?ip=" + ip, "#directoryUsed");
// 		displayNetUsed("../fzjc/getNetData?ip=" + ip, "#netUsed", 1000*60*10);
// 		//displayCpuUsed("../fzjc/getCpuData?ip=" + ip, "#cpuUsed", 1000*60*10);
// 		displayCpuUsed("../fzjc/test", "#cpuUsed", 1000*60*10);
// 		displayMemoryUsed("../fzjc/getMemoryData?ip=" + ip, "#memoryUsed",1000*60*10);
// 	})
//
// }
// $(function() {
//
// 	baseSourceClick("#collect");
//
// })

/**
 * 资源详情
 */
$('#baseSourceModal').on('shown.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var ip = button.data('ip');
    var modal = $(this);
    modal.find('#baseSourceModalHeader').text("基础资源实时运行情况(" + ip + ")");

    var params = {
        "host": ip,
        "minute": 240
    }

    displayCpuUsed("../fzjc/getCpuData", "#cpuUsed", 1000 * 60 * 10, JSON.stringify(params));
    displayMemoryUsed("../fzjc/getMemoryData", "#memoryUsed", 1000 * 60 * 10, JSON.stringify(params));

    displayNetUsed("../fzjc/getNetData", "#netUsed", 1000 * 60 * 10, JSON.stringify(params));
    directorUsage("../fzjc/getDirectoryUsedData", "#directoryUsed", 1000 * 60 * 10, JSON.stringify(params));


})

$(document).ready(function () {
    //一分钟自动刷新一次
    setInterval(getBase, 60000);

    function getBase() {
        var url = "../fzjc/getBaseEventData";
        var params = {
            "listIp": [
                "10.30.16.223",
                "10.30.16.220",
                "10.0.74.226"
            ],
            "minute": -240
        }
        $.ajax({
            type: 'POST',
            url: url,
            data: JSON.stringify(params),
            dataType: "json",
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            },
            success: function (data2) {
                if ("fail" == data2["result"]) {
                    console.log("获取基础设施告警信息数据失败,失败原因："
                        + data2["message"]);
                } else {
                    var resultdata = data2["resultData"];
                    for (var i in resultdata) {
                        var ary = resultdata[i];
                        var div_ip = $('div[data-ip="' + i + '"]');
                        if (div_ip.length > 0) {
                            div_ip.find('li').removeClass();
                            for (var j in ary) {
                                var num = ary[j];
                                var class_name = num == 0 ? "green" : "red";
                                div_ip.find('li').eq(j).addClass(class_name);
                            }
                        }
                    }
                }
            },
            error: function (e2) {
                console.error(e2)
            }
        });
    }
})