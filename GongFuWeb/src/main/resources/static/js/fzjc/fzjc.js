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
$('#baseSourceModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var ip = button.data('ip');
    var modal = $(this);
    modal.find('#baseSourceModalHeader').text("基础资源实时运行情况("+ip+")");

    var params = {
        "host":ip,
        "minute":120
    }
    directorUsage("../fzjc/getDirectoryUsedData", "#directoryUsed",JSON.stringify(params));
    displayNetUsed("../fzjc/getNetData" , "#netUsed", 1000*60*10,JSON.stringify(params));


    displayCpuUsed("../fzjc/getCpuData", "#cpuUsed", 1000*60*10,JSON.stringify(params));
    displayMemoryUsed("../fzjc/getMemoryData" , "#memoryUsed",1000*60*10,JSON.stringify(params));

})
