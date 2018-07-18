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
                if (result.type == 'fail'){
                    alert("信息有误！"+ result.message);
                }
                if(result.type=='success'){
                    debugger;
                    $("#directory").val(result.Usercatalog);
                    $("#directory").selectpicker('refresh');
				}

            },
            error: function(error){

            }
        });
	});
	$("#timeFormat").change(function () {
		var filename=$("#fileNameSelect").find("option:selected").text();
		var format=$(this).val();
		if(filename!=null){
            $.ajax({
                url: "/dataSourceSetting/formatchange",
                data:{"filename":filename,
                    "format":format},
                type: "post",
                success: function(result){
                    if (result.type == 'fail'){
                        alert("格式有误！"+ result.message);
                       $("#timeFormat").options.selectedIndex = 0; //回到初始状态
                        $("#timeFormat").selectpicker('refresh');

                    }
                    if(result.type=='success'){
                        debugger;
                         $("#fileName").val(result.outfilename)
                    }

                },
                error: function(error){

                }
            });
		}

    })
	
	//点击提交元数据
	$("#submitBtn").click(function(){

		var bool=true;

            //获取所有的属
            var deleteId = $("#fileNameSelect").val();
            var name = $("#name").val();
            var directory = $("#directory").val();
            var fileNametxt = $("#fileName").val();
            var fileName=fileNametxt;
            var timeFormat = $("#timeFormat").val();
            var senderUser = $("#SendUserNameSelect").val();
            var ipAddr = $("#IpAddrSelect").val();
            var dataType = $("#dataType").val();
            var departmentName = $("#departmentName").val();
            var phone = $("#phone").val();
            var useDepartment = $("#useDepartment").val();
            var moniterTimer = $("#moniterTimer").val();
            //表单验证
			if(ipAddr==null ||ipAddr == ""){
				bool=false;

			}
            if(senderUser==null|| senderUser==""){
                bool=false;

            }
          if(deleteId==null|| deleteId==""){
            bool=false;

           }
           if(timeFormat==null || timeFormat==""){
            bool=false;

          }
           if(dataType==null|| dataType==""){
            bool=false;

         }
         if(departmentName==null || departmentName==""){
            bool=false;

          }


        if(name==null || name==""){
            bool=false;

        }
        if(directory==null || directory==""){
            bool=false;

        }
       if(fileName==null || fileName==""){
            bool=false;

        }
        if(phone==null || phone==""){
            bool=false;

        }
        if(useDepartment==null || useDepartment==""){
            bool=false;

        }
        if(moniterTimer==null || moniterTimer==""){
            bool=false;

        }
          if (!bool){
         	alert("你有选项未选择，或有栏目未填写！！！")
			  return ;
		  }

        if (bool){

            var data = {
            	"deleteId":deleteId,
				"name":name,
				"directory":directory,
				"fileName":fileName, "timeFormat":timeFormat
                ,"senderUser":senderUser,"ipAddr":ipAddr,
				"dataType":dataType, "departmentName":departmentName,
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
					alert(error)
                }
            });
		}


	});
	
});