(function($){  
	$.fn.serializeJson=function(){  
	  var serializeObj={};  
	   var array=this.serializeArray();  
		var str=this.serialize();  
		$(array).each(function(){  
			if(serializeObj[this.name]){  
				if($.isArray(serializeObj[this.name])){  
					serializeObj[this.name].push(this.value);  
				}else{  
					serializeObj[this.name]=[serializeObj[this.name],this.value];  
				}  
			}else{  
				serializeObj[this.name]=this.value;   
			}  
		});  
		return serializeObj;  
	};  
})(jQuery);  
$(document).ready(function() {
	var _ctx = adminCtx.hostDev; 
	$('#taskPer').bootstrapTable({
		method: 'post',               //请求方法
		dataType: "json",            //类型json
		minimumCountColumns: 2,      //最低显示2行
		striped: true,               //是否显示行间隔色 
		pagination: true,            //是否显示分页（*）  
		sortOrder: "asc",            //排序方式 
		pageNumber:1,                //初始化加载第一页，默认第一页
		pageSize: 10,                //每页的记录行数（*）
		pageList: [10, 25, 50, 100], //可供选择的每页的行数（*）
		ajaxOptions:{
			headers: {token: localStorage.getItem('$tokenBPM')}
		},
		url: _ctx + "/act/taskSetting/getPermissionList", //这个接口需要处理bootstrap table传递的固定参数,并返回特定格式的json数据
		//默认值为 'limit',传给服务端的参数为：limit, offset, search, sort, order Else
		//queryParamsType:'',   
		//查询参数,每次调用是会带上这个参数，可自定义
		queryParams : function(params) {
			var searchParam = $("#searchForm").serializeJson();
			searchParam.page={};
			searchParam.page.pageNo = params.limit === undefined? "1" :params.offset/params.limit+1;
			searchParam.page.pageSize = params.limit === undefined? -1 : params.limit;
			searchParam.page.orderBy = params.sort === undefined? "" : params.sort+ " "+  params.order;
			return searchParam;
		},
		sidePagination: "server",        //分页方式：client客户端分页，server服务端分页（*）
		contextMenuTrigger:"right",      //pc端 按右键弹出菜单
		contextMenuTriggerMobile:"press",//手机端 弹出菜单，click：单击， press：长按。
		contextMenu: '#context-menu',
		onClickRow: function(row, $el){ },
		onLoadSuccess:function(data){
			if(data.token){
				localStorage.setItem("$tokenBPM",data.token);
			}
			var data = data.data;
			console.log(data)
			$.each(data.rows, function(index,obj) {
				obj.uniqueId = index;
			});
			$('#taskPer').bootstrapTable("load",data)
			console.log(data);
		},
		columns: [
			{
				checkbox: true
				
			}
			,{
				field: 'categoryLabel',
				title: '权限类型',
				sortable: true
			}
			,{
				field: 'name',
				title: '权限名称',
				sortable: true
			}
		]
	}); //结束绑定列表数据
	
	//如果是移动端
	if(navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)){ 
		$('#taskPer').bootstrapTable("toggleView");
	}
	$('#taskPer').on('check.bs.table uncheck.bs.table load-success.bs.table ' +
		'check-all.bs.table uncheck-all.bs.table', function () {
		$('#remove').prop('disabled', ! $('#taskPer').bootstrapTable('getSelections').length);
		$('#edit').prop('disabled', $('#taskPer').bootstrapTable('getSelections').length!=1);
	});
	
	//绑定查询按扭 
	$("#search").click("click", function() { 
		$('#taskPer').bootstrapTable('refresh');
	});
	
	//绑定重置按扭
	$("#reset").click("click", function() {
		$("#searchForm  input[name!='ownerCode']").val("");
		$("#searchForm  select").val("").trigger("change");
		$("#searchForm  .select-item").html("");
		$('#taskPer').bootstrapTable('refresh');
	});
}); //结束document.ready

//获取选择列id		
function getIdSelections() {
	return $.map($("#taskPer").bootstrapTable('getSelections'), function (row) {
		return row.id
	});
}

//批量删除
function deleteAll(){
	jp.confirm('确认要删除选中权限管理记录吗？', function(){
		//jp.loading();  	
		jp.get("${ctx}/oa/setting/taskPermission/deleteAll?ids=" + getIdSelections(), function(data){
			if(data.success){
				$('#taskPer').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}