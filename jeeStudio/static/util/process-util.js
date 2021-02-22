(function($) {
	$.ofu = function () {};
	$.extend($.ofu.prototype, {
		isSubmit: function() {
			return true;
		},
		isDivForm: function() {
			return false;
		},
		serviceRuleArgsInit:false
	});		
})(jQuery);
var ofu = new $.ofu();
var ProcUtil = {
	$layero:"",
	ra:"",//规则变量
	layeroIndex:"",
	$table:"",
    param:"",
    $modalDiv:"",
    formData:"",
    
	init:function($layero,layeroIndex,dictionaryId,ruleArgsJson){
		var _this = this;
		
		_this.$layero = $layero;
		_this.$modalDiv = _this.$layero.find("#act-modal");
		_this.layeroIndex = layeroIndex;
		_this.ra = JSON.parse(ruleArgsJson);
		
		_this.userSelectStatus = "";
		_this.pathSelectStatus = "";
		
		if(typeof ofu.serviceRuleArgsInit == "function"){
			ofu.serviceRuleArgsInit();
			ofu.serviceRuleArgsInit = false;
		}
		
		_this.ruleArgsInit(dictionaryId);
		
		//弹窗modal关闭事件
		_this.$layero.on("click",".modal.fade.in button.close",function(){
			$(this).closest(".modal.fade.in").hide();
			$(this).closest(".modal.fade.in").removeClass("in");
			$layero.find("#act-modal").find("#modal-backgroup").hide();
		});
		
	},
	realSubmit: function (){
		var _this = this;
		var data = myHelper.composeData(_this.$layero.find("#inputForm"));
		data.act.param = JSON.stringify(_this.param);
		if(data.tempLoginName){
			data.tempLoginName = data.tempLoginName.split(",");
		}else{
			data.tempLoginName = [];
		}
		data.act.procDefKey = data.procDefKey;
		data.act.procInsId = data.procInsId;
		delete data.ruleArgsJson;
		
		LayerUtil.ajax({
    		url : layui.admin.basePath + '/dynamic/zform/save',
    		type : 'post',
    		data : JSON.stringify(data),
    		shade: true,
    		success : function(res) {
    			LayerUtil.success(res[LayerUtil.getMsgLang()]);
    			$.each(_this.$table, function(i,item){
    				item.refresh();
    			})
    			layui.layer.close(_this.layeroIndex);
    		}
    	});
	},
	getParamStr: function(row) {
		if(row){
			var paramStr = [];
			if (row.procInsId == null || row.procInsId == "") {
				paramStr = [
					{"button":"暂存","button_EN":"Temporary storage","type":"save","flag":"save"},
					{"button":"提交","button_EN":"Submit","type":"saveAndStart","flag":"saveAndStart"},
					{"button":"取消","button_EN":"Cancel","type":"cancel","flag":"cancel"}
				];
			} else if (row.procTaskPermission == null || row.procTaskPermission.operation == null) {
				paramStr = [
					{"button":"暂存","button_EN":"Temporary storage","type":"saveAndClaim","flag":"saveAndClaim"},
				   	{"button":"提交","button_EN":"Submit","type":"saveAndComplete","flag":"saveAndComplete"},
				   	{"button":"取消","button_EN":"Cancel","type":"cancel","flag":"cancel"}
				];
			} else {
				var btns = row.procTaskPermission.operation.split(',');
				for (var i = 0; i < btns.length; i++) {
					paramStr.push({
						"button":btns[i].split('_')[0] ,
						"type":btns[i].split('_')[1] ,
						"flag":btns[i].split('_')[1] ,
					});
				}
			}
			return paramStr;
		}else{
			var paramStr = [
				{"button":"暂存","button_EN":"Temporary storage","type":"save","flag":"save"},
				{"button":"提交","button_EN":"Submit","type":"saveAndStart","flag":"saveAndStart"},
				{"button":"取消","button_EN":"Cancel","type":"cancel","flag":"cancel"}
			];
			return paramStr;
		}
    },
    
    getUserList: function(paramStr, $table) {
    	var _this = this;
    	
    	_this.$table = $table ? $table : _this.$table;
    	_this.param = paramStr ? paramStr : _this.param;
    	var param = _this.param;
    	
    	var formData = myHelper.composeData(_this.$layero.find("#inputForm"));
		formData.act.param = JSON.stringify(param);
		if(formData.tempLoginName){
			formData.tempLoginName = formData.tempLoginName.split(",");
		}else{
			formData.tempLoginName = [];
		}
		formData.act.procDefKey = formData.procDefKey;
		delete formData.ruleArgsJson;
    	
		if (_this.isNeedUserList(param.type)) {
			LayerUtil.ajax({
				type: "post",
				url: layui.admin.basePath + "/dynamic/zform/getUserList",
				data: JSON.stringify(formData),
				dataType: "json",
				shade: true,
				success: function(data) {
					if (data.data != null && data.data.isNeedUserList == false) {
						_this.realSubmit(formData);
					} else {
						if (data.data != null && data.data.isNeedFlag != null && data.data.isNeedFlag) {
							if (_this.ra == null || _this.ra.hand == null) {
								LayerUtil.warning( layui.admin.lang() ? 'Unable to commit, no eligible branches found' : "无法提交，没有找到符合条件的分支");
							} else {
								if (Object.keys(_this.ra.hand).length == 1) {
									$.each(_this.ra.hand, function(key, value) {
										_this.$layero.find("#flag").val(key);
									});
									_this.getUserList();
								} else if (_this.ra.extend.branchType == "multi") {
									_this.buildMultiPathBox(_this.ra.hand,_this.$layero);
								} else {
									_this.buildSinglePathBox(_this.ra.hand,_this.$layero);
								}
							}
							return false;
						}
						if (data.data == null || data.data.userList == null || data.data.userList.length == 0) {
							LayerUtil.warning( layui.admin.lang() ? 'Unable to commit, no eligible users found' : "无法提交，没有找到符合条件的用户");
							return;
						}
						//data.data.userList.length==1,直接提交唯一用户
						else if (data.data.userList.length == 1) {
							$("#tempLoginName").val(data.data.userList[0].loginName);
							_this.realSubmit(formData);
						}
						//data.data.userList.length!=1,data.data.type==single,单选
						else if (data.data.userList.length != 1 && data.data.type == "single") {
							_this.buildSingleUserBox(data.data.userList);
						}
						//data.data.userList.length!=1,data.data.type==multi,多选
						else if (data.data.userList.length != 1 && data.data.type == "multi") {
							_this.buildMultiUserBox(data.data.userList);
						} 
					}
				}
			});
		} else if (_this.isRollBack(param.type)) {
			LayerUtil.ajax({
				type: "get",
				url: layui.admin.basePath + "/dynamic/zform/rollBackCheck",
				data: {
					procInsId: formData.procInsId
				},
				dataType:"json",
				success:function(data){
					if (data.data.success) {
						_this.realSubmit(formData);
					} else {
						LayerUtil.warning(data.message);
					}
				}
			});
		} else if (_this.isNeedNodeList(param.type)) {
			//指定回退时构造节点单选下拉列表
			LayerUtil.ajax({
				type: "post",
				url: layui.admin.basePath + "/dynamic/zform/getNodeList",
				data: JSON.stringify({
					//id: $("#id").val(), 
					procInsId: formData.procInsId
				}),
				dataType:"json",
				success:function(data){
					if (data.data.success) {
						_this.buildNodeBox(data.data.nodeList);
					} else {
						LayerUtil.warning(data.data.message);
					}
				}
			});
		} else if (_this.isCreateNode(param.type)) {
			_this.buildCreateNodeBox();
		} else if (_this.isDeleteNode(param.type)) {
			LayerUtil.confirm( layui.admin.lang() ? 'Are you sure to reduce the signature?' : "确定减签吗？",function(){
				_this.buildDeleteNodeBox();
			});
		} else if (_this.isTerminate(param.type)) {
			LayerUtil.confirm( layui.admin.lang() ? 'Are you sure to terminate the process?' : "确定终止流程吗？",function(){
				_this.realSubmit();
			});
		} else if (_this.isSend(param.type)) {
			buildNewSendForm();
		} else if (_this.isReceive(param.type)) {
			buildNewReceiveForm();
		} else if (_this.isToDistribute(param.type)) {
			buildNewDistributeForm();
		} 
		else if (_this.isNotify(param.type)) {
			_this.buildNotifyNodeBox();
		} else if (_this.isDistribute(param.type)) {
			_this.buildDistributeNodeBox();
		} else {
			//其他情况直接提交
			_this.realSubmit(formData);
		}
	},
	userSelectStatus:"",
	//人员单选
	buildSingleUserBox:function(userList) {
		var _this = this;
		_this.userSelectStatus = "single";
		var select = $("<select></select>").attr("id", "userSelect").attr("class", "form-control")
			.append(
				$("<option></option>").attr("selected", "selected")
			);
		for (var i = 0; i < userList.length; i++) {
			select.append(
				$("<option></option>").val(userList[i].loginName).html(userList[i].name)
			);
		}
		_this.$modalDiv.find("#userSelectDiv").html("").attr("style", "margin: 20px 5px;").append(select);
		if (_this.ra != null && _this.ra.extend != null && _this.ra.extend.submitToOfficeUser != null) {
			_this.$modalDiv.find("#multiUserSelect").html("");
			_this.$modalDiv.find("#singleUserSelect").show();
		} else {
			_this.$modalDiv.find("#officeUserLi").hide();
		}
		
		_this.$modalDiv.find("#myModal").addClass("in").show();
		_this.$modalDiv.find("#modal-backgroup").show();
	},
	checkSelected:function (){
		var _this = this;
		var selected = false;
		var $tempLoginName = _this.$layero.find("[name='tempLoginName']");
		
		var itemShow = _this.$modalDiv.find(".modal.fade.in").find(".layui-tab-item.layui-show").attr("id");
		if(_this.userSelectStatus == "single"){
			if(itemShow == "less"){
				$tempLoginName.val(_this.$modalDiv.find(".modal.fade.in").find("#userSelect").val());
			}else if(itemShow == "all"){
				$tempLoginName.val(_this.$modalDiv.find(".modal.fade.in").find("#singleUserSelectCustom").val());
			}
		}else if(_this.userSelectStatus == "multi"){
			if(itemShow == "less"){
				var arr = [];
				_this.$modalDiv.find(".modal.fade.in").find("#selected_").find("option").each(function(i,obj) {
					arr.push($(obj).attr("value"));
				});
				$tempLoginName.val(arr.join(","));
			}else if(itemShow == "all"){
				$tempLoginName.val(_this.$modalDiv.find(".modal.fade.in").find("#multiUserSelectCustom").val());
			}
		}
		
		if ($tempLoginName.val()) {
			_this.realSubmit();
		} else {
			LayerUtil.warning(layui.admin.lang() ? 'Please select users' : "请选择用户");
			return false;
		}
	},
	//人员多选
	buildMultiUserBox:function(userList) {
		var _this = this;
		_this.userSelectStatus = "multi";
		var unselect = $("<select></select>").attr("id", "unselect").attr("class", "form-control").attr("style", "height: 200px;").attr("multiple", "multiple");
		for (var i = 0; i < userList.length; i++) {
			unselect.append(
				$("<option></option>").attr("id", userList[i].loginName).attr("class", "unselected").attr("ondblclick", "ProcUtil.fillSelected(this);").val(userList[i].loginName).html(userList[i].name)
			);
		}
		var selected = $("<select></select>").attr("id", "selected_").attr("class", "form-control").attr("style", "height: 200px;").attr("multiple", "multiple");
		
		var selectBtn = $("<div></div>").attr("style", "float: left; width: 48px; padding: 70px 7px;")
			.append(
				$("<a></a>").attr("class", "btn btn-white btn-sm").attr("data-toggle", "tooltip").attr("data-placement", "left").attr("title", "选择").attr("onclick", "ProcUtil.fillSelectedValuesForButton()")
					.append(
						$("<i></i>").attr("class", "glyphicon glyphicon-chevron-right")
					)
			)
			.append(
				$("<a></a>").attr("class", "btn btn-white btn-sm").attr("data-toggle", "tooltip").attr("data-placement", "left").attr("title", "移除").attr("onclick", "ProcUtil.removeSelectedValuesForButton()")
					.append(
						$("<i></i>").attr("class", "glyphicon glyphicon-chevron-left")
					)
			);
		_this.$modalDiv.find("#userSelectDiv").attr("style", "height: 200px; margin: 20px 5px 10px 5px;").html("");
		_this.$modalDiv.find("#userSelectDiv")
			.append($("<div></div>").attr("style", "float: left; width: 360px;").append(unselect))
			.append(selectBtn)
			.append($("<div></div>").attr("style", "float: left; width: 360px;").append(selected));
		
		if (_this.ra != null && _this.ra.extend != null && _this.ra.extend.submitToOfficeUser != null) {
			_this.$modalDiv.find("#singleUserSelect").html("");
			_this.$modalDiv.find("#multiUserSelect").show();
		} else {
			_this.$modalDiv.find("#officeUserLi").hide();
		}
		
		_this.$modalDiv.find("#myModal").addClass("in").show();
		_this.$modalDiv.find("#modal-backgroup").show();
	},
	fillSelected:function(obj, targetId){
		var _this = this;
		if (_this.$modalDiv.find('#selected_').html().indexOf($(obj).val()) == -1) {
			_this.$modalDiv.find('#selected_').append(
				$('<option></option>').val($(obj).attr('id'))
					.attr('class', 'selected')
					.attr('id', $(obj).attr('id'))
					.attr('ondblclick', 'ProcUtil.removeSelectedInput(this),$(this).remove()')
					//.attr('onclick', '$(\'#removeTempId\').val($(this).attr(\'id\'))')
					.html($(obj).html())
			)
		}
	},
	removeSelectedInput:function(obj){
		var _this = this;
		_this.$layero.find('input[id=\'' + $(obj).attr('id') + '\'][class=\'selectedInput\']').dblclick();
	},
	fillSelectedValuesForButton:function(){
		var _this = this;
		var arr = _this.$modalDiv.find('#unselect').val();
		for (var i = 0; i < arr.length; i++) {
			_this.$modalDiv.find('option[id=\'' + arr[i] + '\'][class=\'unselected\']').dblclick();
		}
	},
	removeSelectedValuesForButton:function(){
		var _this = this;
		//$('option[id=\'' + $('#removeTempId').val() + '\'][class=\'selected\']').dblclick();
		var arr = _this.$modalDiv.find('#selected_').val();
		if(arr){
			for (var i = 0; i < arr.length; i++) {
				_this.$modalDiv.find('option[id=\'' + arr[i] + '\'][class=\'selected\']').dblclick();
			}
		}
	},
	
	pathSelectStatus:"",
	//选择分支
	buildSinglePathBox:function (hand) {
		var _this = this;
		_this.pathSelectStatus = "single";
		var select = _this.$modalDiv.find("#pathSelect").select2({
			width: "100%"
		}).html("");
		$.each(hand, function(key, value){
			value = value.replace(new RegExp("'", "gm"), "");
			var option = $("<option></option>").val(key).html(value);
			select.append(option);
		});
		_this.$modalDiv.find("#myModal3").addClass("in").show();
		_this.$modalDiv.find("#modal-backgroup").show();
	},
	pathSubmit:function(){
		var _this = this;
		if(_this.pathSelectStatus == "single"){
			_this.$layero.find("#flag").val(_this.$modalDiv.find("#pathSelect").val());
		}
		else if(_this.pathSelectStatus == "multi"){
			var arr = [];
			_this.$modalDiv.find(".modal.fade.in").find("#selected_").find("option").each(function(i,obj) {
				arr.push($(obj).attr("value"));
			});
			_this.$layero.find("#flag").val(arr.join(","));
		}
		$("#modalBtn3Close").click();
		_this.getUserList();
	},
	//分支多选
	buildMultiPathBox:function(hand) {
		var _this = this;
		_this.pathSelectStatus = "multi";
		var unselect = $("<select></select>").attr("id", "unselect").attr("class", "form-control").attr("style", "height: 200px;").attr("multiple", "multiple");
		$.each(hand, function(key, value) {
			value = value.replace(new RegExp("'", "gm"), "");
			var option = $("<option></option>").attr("id", key).attr("class", "unselected").attr("ondblclick", "ProcUtil.fillSelected(this, 'tempPathDiv');").val(key).html(value);
			unselect.append(option);
		});
		var selected = $("<select></select>").attr("id", "selected_").attr("class", "form-control").attr("style", "height: 200px;").attr("multiple", "multiple");
		var selectBtn = $("<div></div>").attr("style", "float: left; width: 8%; margin: 60px 0px 0px 2%;")
			.append(
				$("<a></a>").attr("class", "btn btn-white btn-sm").attr("data-toggle", "tooltip").attr("data-placement", "left").attr("title", "选择").attr("onclick", "ProcUtil.fillSelectedValuesForButton()")
					.append(
						$("<i></i>").attr("class", "glyphicon glyphicon-chevron-right")
					)
			)
			.append(
				$("<a></a>").attr("class", "btn btn-white btn-sm").attr("data-toggle", "tooltip").attr("data-placement", "left").attr("title", "移除").attr("onclick", "ProcUtil.removeSelectedValuesForButton()")
					.append(
						$("<i></i>").attr("class", "glyphicon glyphicon-chevron-left")
					)
			);
		_this.$modalDiv.find("#pathSelectDiv").attr("class", "modal-body").attr("style", "height: 240px;").html("");
		_this.$modalDiv.find("#pathSelectDiv")
			.append($("<div></div>").attr("style", "float: left; width: 45%;").append(unselect))
			.append(selectBtn)
			.append($("<div></div>").attr("style", "float: left; width: 45%;").append(selected));
		
		_this.$modalDiv.find("#myModal3").addClass("in").show();
		_this.$modalDiv.find("#modal-backgroup").show();
	},
	//指定回退
	buildNodeBox:function(nodeList) {
		var _this = this;
		var select = $("<select></select>").attr("id", "nodeList").attr("class", "form-control").attr("onchange", "ProcUtil.setValue('tempNodeKey',this.value)")
			.append($("<option></option>").attr("selected", "selected"));
		for (var i = 0; i < nodeList.length; i++) {
			select.append($("<option></option>").val(nodeList[i].taskDefinitionKey).html(nodeList[i].name));
		}
		_this.$modalDiv.find("#nodeSelectDiv").html("").attr("style", "").attr("class", "modal-body").append(select);
		
		_this.$modalDiv.find("#myModal1").addClass("in").show();
		_this.$modalDiv.find("#modal-backgroup").show();
	},
	//加签
	buildCreateNodeBox:function() {
		var _this = this;
		_this.$modalDiv.find("#taskNameInput").val("自定义节点");
		_this.$modalDiv.find("#myModal2").addClass("in").show();
		_this.$modalDiv.find("#modal-backgroup").show();
	},
	createNode:function() {
		var _this = this;
		var assigneeSelect = _this.$modalDiv.find(".modal.fade.in").find("#assigneeSelect").val();
		var taskNameInput = _this.$modalDiv.find(".modal.fade.in").find("#taskNameInput").val();
		if (assigneeSelect == null || assigneeSelect == "") {
			LayerUtil.warning( layui.admin.lang() ? 'Please select personnel' : "请选择人员");
			return;
		}
		LayerUtil.ajax({
			type: "post",
			url: layui.admin.basePath + "/dynamic/zform/createNode",
			data: JSON.stringify({
				"id": _this.$layero.find("#id").val(),
				"procInsId": _this.$layero.find("#procInsId").val(), 
				"act.flag": _this.$layero.find("#flag").val(), 
				"act.procDefKey": _this.$layero.find("#procDefKey").val(), 
				"tempLoginName": assigneeSelect.split(","), 
				"customNodeName": taskNameInput
			}),
			dataType: "json",
			shade: true,
			success: function(data) {
				LayerUtil.success( layui.admin.lang() ? 'Successful endorsement' : "加签成功");
				_this.drawProcPic();
				_this.$modalDiv.find(".modal.fade.in .close").click();
		   	}
	   });
	},
	//减签
	buildDeleteNodeBox:function() {
		var _this = this;
		LayerUtil.ajax({
			type: "post",
			url: layui.admin.basePath + "/dynamic/zform/deleteNode",
			data: JSON.stringify({
				"id": _this.$layero.find("#id").val(),
				"procInsId": _this.$layero.find("#procInsId").val(), 
				"act.flag": _this.$layero.find("#flag").val(), 
				"act.procDefKey": _this.$layero.find("#procDefKey").val()
			}),
			shade: true,
			success: function(data) {
				if (data.data.success) {
					LayerUtil.success(data.data["message"+layui.admin.lang()]);
					_this.drawProcPic();
				} else {
					LayerUtil.warning(data.data["message"+layui.admin.lang()]);
				}
			}
		});
	},
	//知会
	buildNotifyNodeBox:function(){
		var _this = this;
		_this.$modalDiv.find("#notifyModal").addClass("in").show();
		_this.$modalDiv.find("#modal-backgroup").show();
	},
	notifyNode:function() {
		var _this = this;
		var notifyUsersId = _this.$modalDiv.find(".modal.fade.in").find("#notifyUsers").val();
		if (notifyUsersId == null || notifyUsersId == "") {
			LayerUtil.warning("请选择用户");
			return;
		}
		LayerUtil.ajax({
			type: "post",
			url: layui.admin.basePath + "/dynamic/zform/notifyNode",
			data: JSON.stringify({
				"id": _this.$layero.find("#id").val(),
				"procInsId": _this.$layero.find("#procInsId").val(), 
				"act.flag": _this.$layero.find("#flag").val(), 
				"act.procDefKey": _this.$layero.find("#procDefKey").val(),
				"tempLoginName": notifyUsersId.split(",")
			}),
			dataType: "json",
			success: function(data) {
				if (data.data.success) {
					_this.drawProcPic();
					LayerUtil.success(data.data["message"+layui.admin.lang()]);
					_this.$modalDiv.find(".modal.fade.in .close").click();
				} else {
					LayerUtil.warning(data.data["message"+layui.admin.lang()]);
				}
			}
		});
	},
	//分发 转发
	buildDistributeNodeBox:function() {
		var _this = this;
		_this.$modalDiv.find("#distributeModal").addClass("in").show();
		_this.$modalDiv.find("#modal-backgroup").show();
	},
	distributeNode:function() {
		var _this = this;
		var distributeUsersId = $("#distributeUsers").val();
		if (distributeUsersId == null || distributeUsersId == "") {
			LayerUtil.warning( layui.admin.lang() ? 'Please select users' : "请选择用户");
			return;
		}
		LayerUtil.ajax({
			type:"post",
			url:layui.admin.basePath + "/dynamic/zform/distributeNode",
			data: JSON.stringify({
				"id": _this.$layero.find("#id").val(),
				"procInsId": _this.$layero.find("#procInsId").val(), 
				"act.flag": _this.$layero.find("#flag").val(), 
				"act.procDefKey": _this.$layero.find("#procDefKey").val(),
				"tempLoginName": distributeUsersId.split(",")
			}),
			dataType:"json",
			success:function(data) {
				if (data.data.success) {
					_this.drawProcPic();
					LayerUtil.success(data.data["message"+layui.admin.lang()]);
					_this.$modalDiv.find(".modal.fade.in .close").click();
				} else {
					LayerUtil.warning(data.data["message"+layui.admin.lang()]);
				}
			}
		});
	},
	
	//根据按钮对应的类型判断是否需要查询一个用户列表
	isNeedUserList:function(type){
		var typeArr = ['saveAndStart','saveAndComplete'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isNeedNodeList:function(type){
		var typeArr = ['saveAndSuperReject'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isCreateNode:function(type){
		var typeArr = ['saveAndCreateNode'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isDeleteNode:function(type){
		var typeArr = ['saveAndDeleteNode'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isTerminate:function(type){
		var typeArr = ['saveAndTerminate'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isSend:function(type){
		var typeArr = ['saveAndSend'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isReceive:function(type){
		var typeArr = ['saveAndReceive'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isNotify:function(type){
		var typeArr = ['saveAndNotify'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isDistribute:function(type){
		var typeArr = ['saveAndDistribute'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isToDistribute:function(type){
		var typeArr = ['saveAndToDistribute'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	isRollBack:function(type){
		var typeArr = ['saveAndReject'];
		for (var i = 0; i < typeArr.length; i++) {
			if (typeArr[i] == type) {
				return true;
			}
		}
		return false;
	},
	
	//渲染规则变量
	ruleArgsInit:function(dictionaryId){
		var _this = this;
		var formObj = _this.$layero;
		layui.admin.getDictionary('page-form-field', dictionaryId, function (d) {
    		$.each(d, function (idx, item) {
    			var $obj = formObj.find('[name="' + item.member + '"]');
    			var componentType = $obj.attr("component-type");
    			switch (componentType){
                	case "input":
                		$obj.attr("disabled",true);
                		break;
                	case "textarea":
                		$obj.attr("disabled",true);
                		break;
                	case "select":
                		$obj.attr("disabled",true);
                		break;
                	case "checkbox":
                		$obj.attr("disabled",true);
                		break;
                	case "radio":
                		$obj.attr("disabled",true);
                		break;
                	case "datePicker":
                		$obj.attr("disabled",true);
                		break;
                	case "treeSelect":
                		$obj.attr("disabled",true);
                		break;
                	case "iconSelect":
                		$obj.attr("disabled",true);
                		break;
                	case "grid":
                		$obj.attr("disabled",true);
                		break;
                	case "upload":
                		$obj.attr("disabled",true);
                		break;
                	case "uploadsec":
                		$obj.attr("disabled",true);
                		break;
                	default:
                		break;
                }
			});
			formObj.find('[name="act.comment"]').closest(".adapt").hide();
			formObj.find('[name="act.log"]').closest(".adapt").hide();
		});
		
		var zzb = {
			addBtn:false,
			editBtn:false,
			delBtn:false
		}
		
		if (ofu.isDivForm()) {
		} else {
			if (_this.ra != null && _this.ra.form !=null) {
				$.each(_this.ra.form, function(key, value) {
					//新增针对主子表内控制列表按钮规则变量
					if(key=="addBtn"){
						if (value[0] == "1") {
						 	zzb.addBtn = true;
						}
					}else if(key=="editBtn"){
						if (value[0] == "1") {
						 	zzb.editBtn = true;
						}
					}else if(key=="delBtn"){
						if (value[0] == "1") {
						 	zzb.delBtn = true;
						}
					}else{
						var $obj = formObj.find('[name="' + key + '"]');
						//value[0]控制display
						if (value[0] == "0") {
							$obj.closest(".adapt").hide();
						} else if (value[0] == "1") {
							if(key == "act.comment"){
								formObj.find('[name="act.comment"]').closest(".adapt").show();
							}
							else if(key == "act.log"){
								formObj.find('[name="act.log"]').closest(".adapt").show();
							}
						}
						//value[1]控制disabled
						if (value[1] == "0") {
						} else if (value[1] == "1") {
							$obj.attr("disabled",false);
						}
						//value[2]控制required
						if (value[2] == "0") {
						} else if (value[2] == "1") {
							$obj.addClass("required");
						}
					}
				});
			}
		}
		
		var styleStr = "";
		if(!zzb.addBtn){
			styleStr += _this.$layero.selector+" #addBtn{display:none !important;} "
		}
		if(!zzb.editBtn){
			styleStr += _this.$layero.selector+" .show-btn-group .edit{display:none !important;} "
		}
		if(!zzb.delBtn){
			styleStr += _this.$layero.selector+" .show-btn-group .del{display:none !important;} "
		}
		$("<style></style>").text(styleStr).appendTo(formObj);
	},
	
	isCacheData:function(row){
	   	var returnData = null;
	  	LayerUtil.ajax({
			type: "get",
			url: layui.admin.basePath + "/dynamic/zform/isCacheData",
			data: {
				"processInstanceId":row.procInsId
			},
			async:false,
			dataType: "json",
			shade: true,
			success: function(data) {
				returnData = data.data.data;
				/*if (data.data.result) {
					status = false;
					LayerUtil.warning(data.data["message"+layui.admin.lang()]);
				} else { 
					status = true;
				}*/
		   	}
	   });
	   return returnData;
   	},
   	isCacheView:function(row){
	   	var returnData = null;
	   	LayerUtil.ajax({
			type: "get",
			url: layui.admin.basePath + "/dynamic/zform/isCacheView",
			data: {
				"processInstanceId":row.procInsId
			},
			async:false,
			dataType: "json",
			shade: true,
			success: function(data) {
				returnData = data.data.data;
				/*if (data.data.result) {
					status = false;
					LayerUtil.warning(data.data["message"+layui.admin.lang()]);
				} else { 
					status = true;
				}*/
		   	}
	   	});
	   	return returnData;
   	},
	
	//赋值
	setValue:function(id,value){
		this.$layero.find("#"+id).val(value);
	},
	picDom:"",
	picData:"",
	//获取流程图
	drawProcPic:function($dom,data){
		var _this = this;
		_this.picDom = $dom ? $dom : _this.picDom;
		_this.picData = data ? data : _this.picData;
		LayerUtil.ajax({
			type: "get",
			url: layui.admin.basePath + "/dynamic/zform/tracePhoto",
			data: {
				procDefId: _this.picData.act.procDefId,
				procInsId: _this.picData.procInsId
			},
			dataType: "json",
			success: function(data) {
				_this.picDom.html("").append("<img style='width:100%;' src='data:image/png;base64,"+data.data.data+"'>");
		   	}
	   });
	},
	//获取流程日志
	drawProcLog:function($dom,data){
		LayerUtil.ajax({
			type: "get",
			url: layui.admin.basePath + "/dynamic/zform/histoicFlow",
			data: {
				procInsId: data.procInsId
			},
			dataType: "json",
			success: function(data) {
				var table = $("<table></table>").addClass("table table-hover bootstrap-table");
				var tr = $("<tr></tr>");
				var th1 = $("<th></th>").html("执行环节");
				var th2 = $("<th></th>").html("执行人");
				var th3 = $("<th></th>").html("开始时间");
				var th4 = $("<th></th>").html("结束时间");
				var th5 = $("<th></th>").html("任务历时");
				var th6 = $("<th></th>").html("审批意见");
				if(layui.admin.lang()){
					th1.html("Execution link");
					th2.html("Executor");
					th3.html("Start time");
					th4.html("End time");
					th5.html("Task duration");
					th6.html("Approval opinion");
				}
				
				var thead = $("<thead></thead>");
				table.append(thead.append(tr.append(th1).append(th2).append(th3).append(th4).append(th5).append(th6)));
				
				$.each(data.data.data, function(i, item) {
					var tr = $("<tr></tr>");
					var td1 = $("<td></td>");
					td1.html(item.actMap.histTaskName);
					
					var td2 = $("<td></td>");
					if (item.actMap.assignee == null || item.actMap.assignee == "") {
						//预留
					} else {
						td2.html(item.actMap.assignee);
						//td2.html("<a onclick='openModakUser(this)' userMessage="+JSON.stringify(item.actMap.userMessage)+"><span style='color:#1E9FFF;'>"+item.actMap.assignee+"</span></a>");
					}
					
					var td3 = $("<td></td>");
					if (item.actMap.startTime == null || item.actMap.startTime == "") {
						//预留
					} else {
						td3.html(item.actMap.startTime);
					}
					
					var td4 = $("<td></td>");
					if (item.actMap.endTime == null || item.actMap.endTime == "") {
						//预留
					} else {
						td4.html(item.actMap.endTime);
					}
					
					var td5 = $("<td></td>");
					if (item.actMap.durationTime == null || item.actMap.durationTime == "") {
						//td5.html("小于 1 秒");
					} else {
						td5.html(item.actMap.durationTime);
					}
					
					var td6 = $("<td></td>");
					if (item.comment == null) {
						/*//预留
						if(i == list.length-1){
							td6.html("<a onclick='sendMsgUser(this)' userMessage="+JSON.stringify(item.actMap.userMessage)+"><span style='color:#1E9FFF;'>提醒</span></a>");
						}*/
					} else {
						td6.html(item.comment);
					}
					table.append(tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6));
				});	
				$dom.html("").append(table);
		   	}
	   });
	},
	
}
