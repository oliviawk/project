$(document).ready(function() {

    //select2在modal中无法输入解决方案
    $.fn.modal.Constructor.prototype.enforceFocus = function () {};

    //点击获取所有可能数据的IP
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
                var options = "<option value=''>请选择</option>";
                for (var i = 0; i < result.length; i++) {
                    options += "<option value='"+ result[i] +"'>"+ result[i] +"</option>";
                }
                $("#IpAddrSelect").html(options);
                $("#IpAddrSelect").select2();
                $("#SendUserNameSelect").select2();
            },
            error: function(error){

            }
        });
    });

    //选择ip后给获取该ip下的用户
    $("#IpAddrSelect").change(function(){
        $("#timeformat").val("");
        $.ajax({
            url: "/dataSourceSetting/getPossibleNeedDataSendUserByIpAddr",
            data:{"ipAddr":$(this).find("option:selected").text()},
            beforeSend:function () {
                if ($("#IpAddrSelect").select2("val") == ""){
                    return false;
                }
            },
            type: "post",
            success: function(result){
                var options = "<option value=''>请选择</option>";
                for (var i = 0; i < result.length; i++) {
                    options += "<option value='"+ result[i] +"'>"+ result[i] +"</option>";
                }
                $("#SendUserNameSelect").html(options);
                $("#SendUserNameSelect").select2();
            },
            error: function(error){

            }
        });
    });

    $("#fileNameSelect").select2({
        ajax: {
            url: "getPossibleNeedDataFileNameByIpAddrAndSendUserAndFileName",
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    "ipAddr":$("#IpAddrSelect").select2("val"),
                    "sendUser":$("#SendUserNameSelect").select2("val"),
                    "fileName":params.term
                };
            },
            type: "post",
            beforeSend:function () {
                if ($("#SendUserNameSelect").select2("val") == "" || $("#SendUserNameSelect").select2("val") == "" ){
                    alert("请选择IP和用户");
                    return;
                }
            },
            processResults: function (data, params) {
                return {
                    results: data
                };
            }
        },
        escapeMarkup: function (markup) {
            return markup;
        }, // let our custom formatter work
        minimumInputLength: 3,
        language: "zh-CN", //设置 提示语言
        width:"50%",
        maximumSelectionLength: 1,  //设置最多可以选择多少项
        placeholder: "请选择",
        tags: false,  //设置必须存在的选项 才能选中
        templateResult: function (dt) { //搜索到结果返回后执行，可以控制下拉选项的样式
            if (dt == null){
                return "数据有误";
            }
            var markup = "<option value='"+dt.id+"'>" + dt.fileName + "</option>";
            return markup;
        },
        templateSelection: function (repo) {
            //选中某一个选时执行
            var str=repo.fileName;
            var strfile;
            var strarray;
            if(str!=null||str!=""){
                strfile=""+str;
                strarray=strfile.split("/");
                strfile=strarray[strarray.length-1];
            }
            $("#directory").val(repo.Usercatalog);
            $("#fileName").val(strfile);
            $("#fileNameHidden").val(repo.fileName);
            return repo.fileName;
        }
    });

    $("#fileNameClear").click(function () {
        $("#fileNameSelect").val(null).trigger("change");
    })

    $("#fileNameSelect").change(function () {
        findUserFile();
    })

    $("#timeFormat").change(function () {
        debugger
        // var filename=$("#fileNameSelect").val();
        var filename = $("#fileNameHidden").val();
        var formatone=$(this).val();
        var format=$(this).find("option:selected").text();
        if(format==null||format==""){
            alert("日期格式为"+format+"格式"+formatone)
            return ;
        }
        if(filename==null || filename == ""){
            $(this).val("");
            alert("请选择文件名！！！")
            return ;
        }else{
            $.ajax({
                url: "/dataSourceSetting/formatchange",
                data:{"filename":filename,
                    "format":format},
                type: "post",
                success: function(result){
                    if (result.type == 'fail'){
                        alert("格式有误！"+ result.message+result.leng);
                        $("#timeFormat")[0].selectedIndex = 0;
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
        $("#submitBtn").attr("disabled",true);
        var bool=true;

        //获取所有的属
        var deleteId = $("#fileNameHidden").val();
        var name = $("#name").val().trim();
        var directory = $("#directory").val();
        var fileNametxt = $("#fileName").val();
        var fileName=fileNametxt;
        var timeFormat = $("#timeFormat").val();
        var senderUser = $("#SendUserNameSelect").select2("val");
        var ipAddr = $("#IpAddrSelect").select2("val");
        var dataType = $("#dataType").val();
        var departmentName = $("#departmentName").val();
        var phone = $("#phone").val();
        var useDepartment = $("#useDepartment").val();
        var moniterTimer = $("#moniterTimer").val();
        //表单验证
        if(ipAddr==null ||ipAddr.trim() == ""){
            bool=false;

        }
        if(senderUser==null|| senderUser.trim()==""){
            bool=false;

        }
        if(deleteId==null|| deleteId.trim()==""){
            bool=false;

        }
        if(dataType==null|| dataType.trim()==""){
            bool=false;

        }
        if(departmentName==null || departmentName.trim()==""){
            bool=false;

        }
        if(name==null || name.trim()==""){
            bool=false;

        }
        if(directory==null || directory.trim()==""){
            bool=false;

        }
        if(fileName==null || fileName.trim()==""){
            bool=false;

        }
        if(phone==null || phone.trim()==""){
            bool=false;

        }
        if(useDepartment==null || useDepartment.trim()==""){
            bool=false;

        }
        if (!bool){
            alert("你有选项未选择，或有栏目未填写！！！")
            $("#submitBtn").attr("disabled",false);
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
            // console.log(data)
            $.ajax({
                url: "/dataSourceSetting/insertDataSource",
                data: data,
                type: "post",
                success: function(result){
                    if (result.type == 'fail'){
                        alert("添加失败！  "+ result.message);
                        $("#submitBtn").attr("disabled",false);
                    }else{
                        alert("添加成功!!"+ result.message);
                        $("#submitBtn").attr("disabled",false);
                        //关闭模态框
                        $('#insertDataSource').modal('hide');
                        $("#dataSourceTable").bootstrapTable('refresh');
                    }
                },
                error: function(error){
                    $("#submitBtn").attr("disabled",false);
                    //关闭模态框
                    alert(error+"系统异常添加失败")
                }
            });
        }


    });


});

function findUserFile() {
    console.info("执行findUserFile()方法！！")
    var userIp =$("#IpAddrSelect").select2('val');
    var userName = $("#SendUserNameSelect").select2("val");
    var fileName = $("#fileNameHidden").val();
    if (fileName == ''||userIp==""){
        return ;
    }
    console.info("ip地址："+userIp)
    // console.info(userName)
    // console.info(userIp)
    $.ajax({
        url: "/dataSourceSetting/finAllUsercatalog",
        data:{
            "User_catalog_name":userName,
            "Userfile":fileName,
            "User_ip":userIp
        },
        type: "post",
        success: function(result){
            if (result.type == 'fail'){
                alert("信息有误！"+ result.message);
            }
            if(result.type=='success'){
                debugger;
                $("#directory").val(result.Usercatalog);
            }

        },
        error: function(error){
            console.error(error)
        }
    });
}