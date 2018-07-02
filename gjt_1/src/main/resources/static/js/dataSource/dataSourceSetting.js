$(function(){
	
	//点击获取所有可能数据
	$("#insertDataSourceBtn").click(function(){
		//清空模态框
		$("#name").val("");
		$("#directory").val("");
		$("#fileName").val("");
		$("#timeFormat").val("");
		$("#dataType").val("");
		$("#departmentName").val("");
		$("#phone").val("");
		$("#useDepartment").val("");
		$("#moniterTimer").val("");
		
		$.ajax({
			url: "/dataSourceSetting/getPossibleNeedDataIpAddr",
			type: "post",
			success: function(result){
				debugger;
				var options = "";
				for (var i = 0; i < result.length; i++) {
					options += "<option value='"+ result[i] +"'>"+ result[i] +"</option>";
				}
				$("#IpAddrSelect").html(options);
	            $("#IpAddrSelect" ).selectpicker('refresh');
	            
			}, 
			error: function(error){
				
			}
		});
	});
	
	//选择ip后给获取该ip下的用户
	$("#IpAddrSelect").change(function(){
		$.ajax({
			url: "/dataSourceSetting/getPossibleNeedDataSendUserByIpAddr",
			data:{"ipAddr":$(this).find("option:selected").text()},
			type: "post",
			success: function(result){
				debugger;
				var options = "";
				for (var i = 0; i < result.length; i++) {
					options += "<option value='"+ result[i] +"'>"+ result[i] +"</option>";
				}
				$("#SendUserNameSelect").html(options);
	            $("#SendUserNameSelect" ).selectpicker('refresh');
	            
			}, 
			error: function(error){
				
			}
		});
	});
    // //根据用户名查询用户目录
    // $("#directoryEdit").change(function () {
		// $.ajax({
    //         url: "/dataSourceSetting/finAllUsercatalog",
    //         data:{"User_catalog_name":$(".form-group ").val()},
    //         type: "post",
    //         success: function(result){
    //             debugger;
    //
    //             $("#directoryEdit").value(result);
    //             $("#directoryEdit").selectpicker('refresh');
    //
    //         },
    //         error: function(error){
    //
    //         }
		// });
    //
    // });
	
	//选择用户后给获取该用户下的文件名
	$("#SendUserNameSelect").change(function(){
		$.ajax({
			url: "/dataSourceSetting/getPossibleNeedDataFileNameByIpAddrAndSendUser",
			data:{"ipAddr":$("#IpAddrSelect").find("option:selected").text(),
				"sendUser":$(this).find("option:selected").text()},
			type: "post",
			success: function(result){
				debugger;
				var options = "";
				for (var i = 0; i < result.length; i++) {
					options += "<option value='"+ result[i].id +"'>"+ result[i].fileName +"</option>";
				}
				$("#fileNameSelect").html(options);
	            $("#fileNameSelect" ).selectpicker('refresh');
	            
			}, 
			error: function(error){
				
			}
		});
	});


	//选择文件名后给其他输入框添加文件名
	$("#fileNameSelect").change(function(){
		debugger;
		var selected = $(this).find("option:selected").text();
		var  result=selected.split("/");
		// var siteNum = selected.lastIndexOf("/");
		// var directory = selected.substring(0, siteNum + 1);
		// $("#directory").val(directory);
		// var fileName = selected.replace(directory, "");
		$("#fileName").val(result[result.length-1]);

        $.ajax({
            url: "/dataSourceSetting/finAllUsercatalog",
            data:{"User_catalog_name":$("#SendUserNameSelect").find("option:selected").text(),
			"Userfile":$(this).find("option:selected").text()},
            type: "post",
            success: function(result){
                debugger;
                $("#directory").val(result.Usercatalog);
                $("#directory").selectpicker('refresh');

            },
            error: function(error){

            }
        });
	});
	
	//点击提交元数据
	$("#submitBtn").click(function(){
		//获取所有的属性
		debugger;
		var deleteId = $("#fileNameSelect").val();
		var name = $("#name").val();
		var directory = $("#directory").val();
		var fileName = $("#fileName").val();
		var timeFormat = $("#timeFormat").val();
		var senderUser = $("#SendUserNameSelect").val();
		var ipAddr = $("#IpAddrSelect").val();
		var dataType = $("#dataType").val();
		var departmentName = $("#departmentName").val();
		var phone = $("#phone").val();
		var useDepartment = $("#useDepartment").val();
		var moniterTimer = $("#moniterTimer").val();
		
		var data = {"deleteId":deleteId, "name":name, "directory":directory, "fileName":fileName, "timeFormat":timeFormat
				,"senderUser":senderUser,"ipAddr":ipAddr, "dataType":dataType, "departmentName":departmentName,
				"phone":phone, "useDepartment":useDepartment, "moniterTimer":moniterTimer};
		$.ajax({
			url: "/dataSourceSetting/insertDataSource",
			data: data,
			type: "post",
			success: function(result){
				if (result.type == 'fail'){
					alert("添加失败！  "+ result.message);
				}else{
					alert("添加成功!!"+ result.message);
					//关闭模态框
					$('#insertDataSource').modal('hide');
				}
			}, 
			error: function(error){
				
			}
		});
	});
	
});