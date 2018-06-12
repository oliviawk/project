/**
 * 获取 数据-环节 关系数据
 */
function getDataModuleRelation() {
    $.ajax({
        type: "GET",
        url: "../subscription/getdatamodulerelation",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (resultData) {
            var tboty_html = "";
            if (resultData != null){
                $.each(resultData,function (i,dt) {
                    tboty_html += "" +
                        "<tr>" +
                        "   <td>"+dt[1]+"</td>" +
                        "   <td>"+dt[2]+"</td>" +
                        "   <td>"+dt[3]+"</td>" +
                        "   <td>"+dt[4]+"</td>" +
                        "</tr>";
                })
            }
            $("#data_module_relation_tab").html(tboty_html);
        },
        error: function (err) {
//            alert(err);
            console.error(err)
            $("#data_module_relation_tab").html("");
        }
    });

}

function initSelectsData(){

    $.ajax({
        type: "GET",
        url: "../subscription/getselectsdata",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (resultData) {
            console.info(resultData)
            if(resultData != null){
                var moduleInfo = resultData.moduleInfo;
                var sourceDataInfo = resultData.sourceDataInfo;

                $.each(sourceDataInfo , function (i , dt) {
                    $("#sourceNameMd").append("<option value='"+dt.pk_id+"'>"+dt.file_name+"_"+dt.sub_name+"</option>");
                    $("#produceNameMd").append("<option value='"+dt.pk_id+"'>"+dt.file_name+"_"+dt.sub_name+"</option>");
                })
            }

        },
        error: function (err) {
            console.error(err)
        }
    });
}

/**
 * 历史数据弹出框
 */
$('#SCModal').on('show.bs.modal', function (event) {
    // var sourceName = $("#sourceName").val();
    // var produceName = $("#produceName").val();
    //
    // $("#sourceNameMd").val(sourceName);
    // $("#produceNameMd").val(produceName);


})


function addDataModuleRelation(){
    var param = {
        "sourceName":$("#sourceNameMd").val(),
        "produceName":$("#produceNameMd").val(),
        "moduleName":$("#moduleNameMd").val(),
        "IP":$("#IPMd").val(),
    }
    console.log(param)

    $.ajax({
        type: "POST",
        url: "../subscription/adddatamodulerelation",
        contentType: "application/json",
        dataType: "json",
        // async:false,
        data:JSON.stringify(param),
        success: function (rd) {
            console.info(rd)
            if (rd != null){
                alert(rd.resultData);
            }
        }
    });
    $('#SCModal').modal('hide')
}



$(function(){
    getDataModuleRelation();
    initSelectsData();
});