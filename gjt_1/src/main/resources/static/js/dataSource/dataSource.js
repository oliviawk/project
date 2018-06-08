var alertLevel;	//告警级别：正常、严重、紧急、警告
var queryType	//查询类型：所有、告警
$(function(){
	var oTable = new TableInit();
	oTable.init();

	$(".alertLevelBtn").click(function(){
		debugger;
		queryType = null;
		alertLevel = $(this).html();
		$("#dataSourceTable").bootstrapTable("refresh");	//刷新表格
//		$("#dataSourceTable").bootstrapTable('destroy');
//		oTable.init();
	});
	
	$(".queryTypeBtn").click(function(){
		debugger;
		alertLevel = null;
		queryType = $(this).html();
		$("#dataSourceTable").bootstrapTable("refresh");	//刷新表格
//		$("#dataSourceTable").bootstrapTable('destroy');
//		oTable.init();
	});
	
});

var TableInit = function(){
	var oTableIint = new Object();
	//初始化Table
	oTableIint.init = function(){
		$("#dataSourceTable").bootstrapTable({
			url: '/dataSource/getAllTableData',         //请求后台的URL（*）
			method: 'post',                      //请求方式（*）
			contentType : "application/x-www-form-urlencoded",
//			toolbar: '#toolbar',                //工具按钮用哪个容器
			showColumns:true,
			striped: true,                      //是否显示行间隔色
			cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
			pagination: true,                   //是否显示分页（*）
//			sortable: true,                     //是否启用排序
//			sortOrder: "asc",                   //排序方式
			queryParams: oTableIint.queryParams,//传递参数（*）
			sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
			pageNumber: 1,                       //初始化加载第一页，默认第一页
			pageSize: 2,                       //每页的记录行数（*）
			pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
//			height: 700,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
			uniqueId: "no",                     //每一行的唯一标识，一般为主键列
			columns: [{
				field: 'fields.department_name',
				title: '单位',
				sortable: true
			},{
				field: 'name',
				title: '数据名称',
				sortable: true
			},{
				field: 'aging_status',
				title: '状态',
				sortable: true
			},{
				field: 'fields.data_type',
				title: '资料类型',
				sortable: true
			},{
				field: 'fields.ip_addr',
				title: 'IP地址',
				sortable: true
			},{
				field: 'should_time',
				title: '应到时间',
				sortable: true
			},{
				field: 'fields.file_size',
				title: '文件大小(MB)',
				sortable: true
			},{
				field: 'fields.event_info',
				title: '描述信息',
				sortable: true
			},{
				field: 'fields.use_department',
				title: '影响业务',
				sortable: true
			}]
		});
	};
	
	//得到查询的参数
	oTableIint.queryParams = function (params) {
		debugger;
		var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
				limit: params.limit,   //页面大小
				offset:params.offset,
				sort: params.sort,
				order: params.order,
				alertLevel: alertLevel,
				queryType: queryType
		};
		return temp;
	};
	return oTableIint;
};