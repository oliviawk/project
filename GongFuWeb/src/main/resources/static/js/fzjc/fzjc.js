function baseSourceClick(id) {
	$(id).click(function() {
		var ip;
		if (id == "#collect") {
			ip = "10.30.16.220";
		}
		directorUsage("../fzjc/getDirectoryUsedData?ip=" + ip, "#directoryUsed");
		displayNetUsed("../fzjc/getNetData?ip=" + ip, "#netUsed", 1000*60*10);
		//displayCpuUsed("../fzjc/getCpuData?ip=" + ip, "#cpuUsed", 1000*60*10);
		displayCpuUsed("../fzjc/test", "#cpuUsed", 1000*60*10);
		displayMemoryUsed("../fzjc/getMemoryData?ip=" + ip, "#memoryUsed",1000*60*10);
	})

}
$(function() {

	baseSourceClick("#collect");

})