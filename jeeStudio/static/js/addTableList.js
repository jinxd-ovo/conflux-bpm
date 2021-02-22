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
	//渲染select框
	$.ajax({
		type: 'post',
		url: _ctx + "/act/taskSetting/genTableList",
		headers: {
			token: localStorage.getItem('$tokenBPM')
		},
		success:function(data){
			if(data.token){
				localStorage.setItem("$tokenBPM",data.token);
			}
			var strH = [];
			strH.push({
				id:'',
				text: '无'
			});

			for(var i=0;i<data.data.genTableList.length;i++){
				strH.push({
					id: data.data.genTableList[i].name,
					text: data.data.genTableList[i].nameAndComments
				})
				//strH += '<option value="'+data.data.genTableList[i].name+'">'+data.data.genTableList[i].nameAndComments+'</option>'
			}

			$("#parentTable").select2({
				width: '195px',
				data: strH
			})
			$("#parentTable").trigger('change');
		}
	})
	//渲染列表
	$('#addTable').bootstrapTable({
		method: 'get',               //请求方法
		dataType: "json",            //类型json
		minimumCountColumns: 2,      //最低显示2行
		striped: true,               //是否显示行间隔色 
		pagination: false,            //是否显示分页（*）  
		sortOrder: "asc",            //排序方式 
		pageNumber:1,                //初始化加载第一页，默认第一页
		pageSize: 10,                //每页的记录行数（*）
		ajaxOptions:{
			headers: {token: localStorage.getItem('$tokenBPM')}
		},
		pageList: [10, 25, 50, 100], //可供选择的每页的行数（*）
		url: _ctx + "/act/taskSetting/genTableColumnList", //这个接口需要处理bootstrap table传递的固定参数,并返回特定格式的json数据
		//默认值为 'limit',传给服务端的参数为：limit, offset, search, sort, order Else
		//queryParamsType:'',   
		//查询参数,每次调用是会带上这个参数，可自定义
		queryParams : function(params) {

			var searchParam = $("#searchForm").serializeJson();
			//console.log(params);
			//alert(typeof searchParam);
			// searchParam.page={};
			// searchParam.page.pageNo = params.limit === undefined? "1" :params.offset/params.limit+1;
			// searchParam.page.pageSize = params.limit === undefined? 10 : params.limit;
			// searchParam.page.orderBy = params.sort === undefined? "" : params.sort+ " "+  params.order;
			return searchParam;
		},
		sidePagination: "server",        //分页方式：client客户端分页，server服务端分页（*）
		contextMenuTrigger:"right",      //pc端 按右键弹出菜单
		contextMenuTriggerMobile:"press",//手机端 弹出菜单，click：单击， press：长按。
		contextMenu: '#context-menu',
		onClickRow: function(row, $el){ },
		onLoadSuccess: function (data) { 
			if(data.token){
				localStorage.setItem("$tokenBPM",data.token);
			}
			var data = data.data;
			//console.log(data)
			$.each(data.rows, function(index,obj) {
				obj.uniqueId = index;
			});
			$('#addTable').bootstrapTable("load",data)
			//console.log(data);
		},
		columns: [
			{
				checkbox: true
				
			}
			,{
				field: 'comments',
				title: '说明',
				sortable: true
			}
			,{
				field: 'javaFieldId',
				title: 'Java属性名称',
				sortable: true
			}
		]
	}); //结束绑定列表数据
	
	//如果是移动端
	if(navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)){ 
		$('#addTable').bootstrapTable("toggleView");
	}
	$('#addTable').on('check.bs.table uncheck.bs.table load-success.bs.table ' +
		'check-all.bs.table uncheck-all.bs.table', function () {
		$('#remove').prop('disabled', ! $('#addTable').bootstrapTable('getSelections').length);
		$('#edit').prop('disabled', $('#addTable').bootstrapTable('getSelections').length!=1);
	});
	
	//绑定查询按扭 
	$("#search").click("click", function() { 
		$('#addTable').bootstrapTable('refresh');
	});
	
	//绑定重置按扭
	$("#reset").click("click", function() {
		$("#searchForm  input[name!='ownerCode']").val("");
		$("#searchForm  select").val("").trigger("change");
		$("#searchForm  .select-item").html("");
		$('#addTable').bootstrapTable('refresh');
	});
}); //结束document.ready

//获取选择列id		
function getIdSelections() {
	return $.map($("#addTable").bootstrapTable('getSelections'), function (row) {
		return row.id
	});
}

//批量删除
function deleteAll(){
	jp.confirm('确认要删除选中权限管理记录吗？', function(){
		//jp.loading();  	
		jp.get("${ctx}/oa/setting/taskPermission/deleteAll?ids=" + getIdSelections(), function(data){
			if(data.success){
				$('#addTable').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}