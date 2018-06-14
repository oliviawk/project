$(function(){
    getsourceData();
});

function getsourceData() {
    $.ajax({
        type: "GET",
        url: "../subscription/getsourcedata",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (sourceData) {
            var tboty_html = "";
            if (sourceData != null){
                $.each(sourceData,function (i,dt) {
                    console.info(dt)
                    tboty_html += "" +
                        "<tr>" +
                        "   <td>"+dt.name+"</td>" +
                        "   <td>"+dt.sub_name+"</td>" +
                        "   <td>"+dt.file_name+"</td>" +
                        "   <td>"+dt.path+"</td>" +
                        "   <td>"+dt.ip_addr+"</td>" +
                        "   <td>"+dt.topic+"</td>" +
                        "   <td><button type='submit' class='btn btn-default' data-toggle='modal' data-target='.bs-example-modal-sm' onclick='cueDel("+dt.pk_id+")'>删除</button></td>" +
                        "</tr>";
                })
            }
            $("#data_module_relation_tab").html(tboty_html);
        },
        error: function (err) {
            console.error(err)
            $("#data_module_relation_tab").html("");
        }
    });

}

function addSourceData(){
    var dataName=$("#dataName").val();
    var otherName=$("#otherName").val();
    var ioName=$("#ioName").val();
    var path=$("#path").val();
    var ip=$("#ip").val();
    var topic=$("#topic").val();
    var params = {
        "name":dataName,
        "path":path,
        "fileName":ioName,
        "subName":otherName,
        "ipAddr":ip,
    }
    if(dataName == "" || otherName == "" || ioName == "" || path == "" || ip == "" || topic == ""){
        alert("请补全信息再添加");
    }else{
        console.log(params);
        //添加数据
        $.ajax({
            type: "POST",
            url: "../subscription/addsourcedata",
            datatype:"json",
            data:JSON.stringify({"name":dataName,"path":path,"fileName":ioName,"subName":otherName,"ipAddr":ip,"topic":topic}),
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            },
            success: function (sourceData) {
                var tboty_html = "";
                if (sourceData != null){
                    $.each(sourceData,function (i,dt) {
                        console.info(dt)
                        tboty_html += "" +
                            "<tr>" +
                            "   <td>"+dt.name+"</td>" +
                            "   <td>"+dt.sub_name+"</td>" +
                            "   <td>"+dt.file_name+"</td>" +
                            "   <td>"+dt.path+"</td>" +
                            "   <td>"+dt.ip_addr+"</td>" +
                            "   <td>"+dt.topic+"</td>" +
                            "   <td><button type='submit' class='btn btn-default' data-toggle='modal' data-target='.bs-example-modal-sm' onclick='cueDel("+dt.pk_id+")'>删除</button></td>" +
                            "</tr>";
                    })
                }
                $("#data_module_relation_tab").html(tboty_html);
            },
            error: function (err) {
                console.error(err);
                $("#data_module_relation_tab").html("");
            }
        });
    }

}

function deletSourceData() {
    var id=$("#hideVlue").val();
    $.ajax({
        type: "POST",
        url: "../subscription/deletsourcedata",
        datatype:"json",
        data:JSON.stringify({"pkId":id}),
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (sourceData) {
            var tboty_html = "";
            if (sourceData != null){
                $.each(sourceData,function (i,dt) {
                    console.info(dt)
                    tboty_html += "" +
                        "<tr>" +
                        "   <td>"+dt.name+"</td>" +
                        "   <td>"+dt.sub_name+"</td>" +
                        "   <td>"+dt.file_name+"</td>" +
                        "   <td>"+dt.path+"</td>" +
                        "   <td>"+dt.ip_addr+"</td>" +
                        "   <td>"+dt.topic+"</td>" +
                        "   <td><button type='submit' class='btn btn-default' data-toggle='modal' data-target='.bs-example-modal-sm' onclick='cueDel("+dt.pk_id+")'>删除</button></td>" +
                        "</tr>";
                })
            }
            $("#data_module_relation_tab").html(tboty_html);
        },
        error: function (err) {
            console.error(err)
        }
    });
}
//删除提示方法
function cueDel(id) {
    $("#hideVlue").val(id);
}