$(function() {

	// 1.初始化Table
	var oTable = new TableInit();
	oTable.Init();

	// 2.初始化Button的点击事件
	var oButtonInit = new ButtonInit();
	oButtonInit.Init();

});
var TableInit = function() {
	var filenametwo;
	var oTableInit = new Object();

	// 初始化Table
	oTableInit.Init = function() {
		$('#dataSourceTable').bootstrapTable({
			url : '/dataSourceSetting/getDataSourceSettingData', // 请求后台的URL（*）
			method : 'get', // 请求方式（*）
			toolbar : '#toolbar', // 工具按钮用哪个容器
			striped : true, // 是否显示行间隔色
			cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
			pagination : true, // 是否显示分页（*）
			sortable : false, // 是否启用排序
			sortOrder : "asc", // 排序方式
			queryParams : oTableInit.queryParams,// 传递参数（*）
			sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
			pageNumber : 1, // 初始化加载第一页，默认第一页
			pageSize : 10, // 每页的记录行数（*）
			pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
			// search: true, //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
			// strictSearch: true,
			showColumns : true, // 是否显示所有的列
			// showRefresh: true, //是否显示刷新按钮
			minimumCountColumns : 2, // 最少允许的列数
			clickToSelect : true, // 是否启用点击选中行
//			height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
			// uniqueId: "ID", //每一行的唯一标识，一般为主键列
			// showToggle:true, //是否显示详细视图和列表视图的切换按钮
			cardView : false, // 是否显示详细视图
			// detailView: false, //是否显示父子表
			columns : [ {
				checkbox : true
			}, {
				title : 'id',
				field : 'pkId'
			}, {
				title : '数据名称',
				field : 'name'
			}, {
				title : '目录',
				field : 'directory'
			}, {
				title : '文件名称',
				field : 'fileName'

			}, {
				title : '时间格式',
				field : 'timeFormat',
				visible:false
                },{
				title : '发送用户',
				field : 'sendUser'
			}, {
				title : '发送地址',
				field : 'ipAddr'
			}, {
				title : '数据类型',
				field : 'dataType'
			}, {
				title : '发送单位',
				field : 'departmentName'
			}, {
				title : '电话',
				field : 'phone'
			}, {
				title : '使用单位',
				field : 'useDepartment'
			}, {
				title : '时次',
				field : 'moniterTimer'
			} ]
		});
	}
	// 得到查询的参数
	oTableInit.queryParams = function(params) {
		var temp = { // 这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
			limit : params.limit, // 页面大小
			offset : params.offset, // 页码
		};
		return temp;
	};
	return oTableInit;
}

var ButtonInit = function () {
    var oInit = new Object();
    var postdata = {};
	var Exitarray;
    oInit.Init = function () {

        $("#btn_edit").click(function () {
            var arrselections = $("#dataSourceTable").bootstrapTable('getSelections');
            if (arrselections.length > 1) {
                alert('只能选择一行进行编辑');

                return;
            }
            if (arrselections.length <= 0) {
                alert('请选择有效数据')

                return;
            }
            Exitarray=arrselections;
            $("#nameEdit").val(arrselections[0].name);
            $("#directoryEdit").val(arrselections[0].directory);
            $("#typeEdit").val(arrselections[0].fileName);
            $("#senderUserEdit").val(arrselections[0].sendUser);
            $("#ipAddrEdit").val(arrselections[0].ipAddr);
            $("#dataTypeEdit").val(arrselections[0].dataType);
            $("#departmentNameEdit").val(arrselections[0].departmentName);
            $("#phoneEdit").val(arrselections[0].phone);
            $("#useDepartmentEdit").val(arrselections[0].useDepartment);
            $("#moniterTimerEdit").val(arrselections[0].moniterTimer);
            $("#idEdit").val(arrselections[0].pkId);
            $("#formattime").val(arrselections[0].timeFormat);
            $('#EditDataSource').modal();
        });

        $("#submitEdit").click(function(){
        	var bol=true;
    		//获取所有的属性
            $("#submitEdit").attr("disabled",true);
    		debugger;

    		var deleteId = $("#fileNameSelectEdit").val();
    		var timeFormat=$("#formattime").val();
    		var name = $("#nameEdit").val();
    		var directory = $("#directoryEdit").val();
    		var type = $("#typeEdit").val();
    		var senderUser = $("#senderUserEdit").val();
    		var ipAddr = $("#ipAddrEdit").val();
    		var dataType = $("#dataTypeEdit").val();
    		var departmentName = $("#departmentNameEdit").val();
    		var phone = $("#phoneEdit").val();
    		var useDepartment = $("#useDepartmentEdit").val();
    		var moniterTimer = $("#moniterTimerEdit").val();
    		var pkId = $("#idEdit").val();
    		if (name==null||name.trim()==""){
    			bol=false;
			}
            if (directory==null||directory.trim()==""){
                bol=false;
            }
            if (senderUser==null||senderUser.trim()==""){
                bol=false;
            }
            if (ipAddr==null||ipAddr.trim()==""){
                bol=false;
            }
            if (departmentName==null||departmentName.trim()==""){
                bol=false;
            }

            if (moniterTimer==null||moniterTimer.trim()==""){
                bol=false;
            }
           if (!bol){
            	alert("请不要填写空字符！！")
               $("#submitEdit").attr("disabled",false);
			   return ;
		   }
    		var data = {"pkId":pkId,"timeFormat":timeFormat, "name":name, "directory":directory, "type":type, "senderUser":senderUser,
    				"ipAddr":ipAddr, "dataType":dataType, "departmentName":departmentName,
    				"phone":phone, "useDepartment":useDepartment, "moniterTimer":moniterTimer, "Exitbefore": JSON.stringify(Exitarray)};
    		$.ajax({
    			url: "/dataSourceSetting/EditDataSource",
    			data: data,
                // traditional: true,
    			type: "post",
    			success: function(result){
    				debugger;
                    if (result.message == 'success'){
                        alert("更新数据库对比内容成功！!");
                        $("#submitEdit").attr("disabled",false);
                    }else{
                        alert("更新数据库对比内容失败!!");
                        $("#submitEdit").attr("disabled",false);

                    }
    				if (result.result == 'success'){
    					alert("修改成功!!");
    					//关闭模态框
                        $("#submitEdit").attr("disabled",false);
    					$('#EditDataSource').modal('hide');
    					$("#dataSourceTable").bootstrapTable('refresh');

    				} else {
                        alert("修改失败！!");
                        $("#submitEdit").attr("disabled",false);
                    }


                },
    			error: function(error){
    				
    			}
    		});
    	
        })
        
        $("#btn_delete").click(function () {
            var arrselections = $("#dataSourceTable").bootstrapTable('getSelections');
            if (arrselections.length <= 0) {
                alert('请选择有效数据');
                return;
            }
            
            var e= confirm('是否删除?');

            if (!e) {
                return;
            }
            
            $.ajax({
                type: "post",
                url: "/dataSourceSetting/deleteDataSource",
                data: { "data": JSON.stringify(arrselections) },
                success: function (data) {
                    if (data.result == "success") {
                        alert('删除数据成功');
                        $("#dataSourceTable").bootstrapTable('refresh');
                    }
                    else {
                        alert('删除数据失败');
                        $("#dataSourceTable").bootstrapTable('refresh');
					}
                },
                error: function () {
                    alert('Error');
                },
                complete: function () {

                }

            });
        
            
        });


    };

    return oInit;
};