//区块链点击操作
$(document).on("click", ".qu-kuai-lian", function() {
	var id = $(this).attr("data-id");
	var name = $(this).attr("data-name");
	window.open(layui.admin.host+'/qkl.html?id='+id+'&name='+name);
});
//区块链点击操作
$(document).on("click", ".qu-kuai-lian-form", function() {
	var id = $(this).closest("#inputForm").find("input#hashKey").val();
	var name = $(this).attr("data-name");
	if(name == 'log') {
		id = name + ',' + $(this).closest("#inputForm").find("input#id").val();
	}
	window.open(layui.admin.host+'/qkl.html?id='+id);
});

//列表页按钮操作
$(document).on("click", "#hardSearch", function() {
	var $parent = $(this).closest(".layui-fluid");
	$parent.find(".searchDiv").removeClass("positionSpc");
	$parent.find("#easySearch").show();
	$(this).hide()
});
$(document).on("click", "#easySearch", function() {
	var $parent = $(this).closest(".layui-fluid");
	$parent.find(".searchDiv").addClass("positionSpc");
	$parent.find("#hardSearch").show();
	$(this).hide()
});
$(document).on("click", "div[component-type='treeSelectOffice'] .tag-div-button", function() {
	var _this = this;
	var $obj = $(_this).closest("div[component-type='treeSelectOffice']");
	var name = $obj.attr("name");
	var actionUrl = $obj.attr("actionUrl");
	var sync = $obj.attr("sync") ? JSON.parse($obj.attr("sync")) : false;
	var check = $obj.attr("check") ? JSON.parse($obj.attr("check")) : false;
	var chooseParent = $obj.attr("chooseParent") ? JSON.parse($obj.attr("chooseParent")) : false;
	var idArr;
	if($obj.find("input[name='" + name + ".id']").val()) {
		idArr = $obj.find("input[name='" + name + ".id']").val().split(",")
	}
	var nameArr;
	if($obj.find("input[name='" + name + ".name']").val()) {
		nameArr = $obj.find("input[name='" + name + ".name']").val().split(",")
	}
	layui.layer.open({
		type: 1,
		id: 'LAY-popup-treeSelectOffice',
		url: 'admin/tag/treeSelectOffice',
		title: layui.admin.lang() ? 'treeSelectOffice' : '请选择',
		area: ['300px', '500px'],
		shadeClose: true,
		closeBtn: false,
		btn: layui.admin.lang() ? ['ok', 'close'] : ['确定', '取消'],
		success: function(layero, index) {
			var _this = this;
			layui.view(_this.id).render(_this.url, typeof _this.initData === 'function' ? _this.initData.call(_this) : _this.initData || null).done(function() {
				var _this = this;
				var to = false;
				var jstreeData;
				$('#carKindjsTreeOffice').jstree({
					'core': {
						"multiple": false,
						"animation": 0,
						"themes": {
							"variant": "large",
							"icons": false,
							"stripes": false
						},
						'data': function(obj, callback) {
							LayerUtil.ajax({
								url: layui.admin.basePath + actionUrl,
								data: _this.initData,
								success: function(res) {
									if(res.code === 0) {
										var _data = res.data.list;
										var _obj = [];
										for(var i = 0; i < _data.length; i += 1) {
											if(_data[i].parentId == null) {
												_data[i].parentId = 0
											}
											if(_data[i].id == 1 && _data[i].parentId == 0) {
												_obj.push({
													id: _data[i].id,
													parent: '#',
													text: _data[i].name,
													parentId: _data[i].parentId
												})
											} else {
												_obj.push({
													id: _data[i].id,
													parent: _data[i].parentId,
													text: _data[i].name,
													parentId: _data[i].parentId
												})
											}
										}
										jstreeData = _obj;
										callback.call(this, _obj)
									}
								}
							})
						}
					},
					"conditionalselect": function(node, event) {
						return false
					},
					'plugins': ['wholerow', "search"]
				}).bind("activate_node.jstree", function(obj, e) {
					var node = $('#carKindjsTreeOffice').jstree(true).get_selected(true)[0];
					var id = node.id;
					var text = node.text;
					var opt = {
						silent: true,
						query: {
							'kind.id': node.id
						}
					};
					$(".layout-tab>li>a").removeClass("active");
					$(".layout-tab>li>a").eq(0).addClass("active");
					$obj.find("input[name='" + name + ".id']").val(id);
					$obj.find("input[name='" + name + ".name']").val(text);
					layui.layer.close(index)
				}).on('loaded.jstree', function(e, data) {
					var isAllExtend = $("#isAllExtend").val();
					if(isAllExtend == "all") {
						data.instance.open_all()
					} else if(isAllExtend == "first") {
						data.instance.open_node(jstreeData[0])
					} else {
						data.instance.open_node(jstreeData[0])
					}
				}).on('ready.jstree', function() {
					$.each(jstreeData, function(i, item) {
						var id = "#carKindjsTreeOffice li#" + item.id + ">.jstree-wholerow";
						if($(id).find(".jsTreeNumDiv").length > 0) {
							$(id).find(".jsTreeNumDiv").remove()
						}
					})
				}).on('after_open.jstree', function(node, obj) {
					$.each(obj.node.children_d, function(i, item) {
						var id = "#carKindjsTreeOffice li#" + item + ">.jstree-wholerow";
						if($(id).find(".jsTreeNumDiv").length > 0) {
							$(id).find(".jsTreeNumDiv").remove()
						}
						var data = $.each(jstreeData, function(i, obj) {
							return obj.id == item
						});
						if(data.length > 0) {}
					})
				});
				window.refreshJsTree = function() {};
				$(document).on("click", ".searchForm-title .toggle", function() {
					$(this).find(".fa").toggleClass("fa-chevron-up");
					$(this).find(".fa").toggleClass("fa-chevron-down");
					$(this).closest("li").find(".searchForm-body").toggle()
				})
			})
		},
		end: function() {
			typeof end === 'function' && end.apply(this, arguments)
		},
		maxmin: 1,
		closeBtn: 1,
		yes: function(index, layero) {
			layui.layer.close(index)
		},
		cancel: function(index) {}
	})
});
var ztreeSyncUrl = "";

function getZtreeSyncUrl(treeId, treeNode) {
	return treeNode.isParent ? layui.admin.basePath + ztreeSyncUrl + "?id=" + treeNode.id : layui.admin.basePath + ztreeSyncUrl
}
$(document).on("click", "div[component-type='treeSelect'] .tag-div-button", function() {
	var _this = this;
	var $obj = $(_this).closest("div[component-type='treeSelect']");
	if($obj.attr("disabled")) {
		return false
	}
	var name = $obj.attr("name");
	var actionUrl = $obj.attr("actionUrl");
	var sync = $obj.attr("sync") ? JSON.parse($obj.attr("sync")) : false;
	var check = $obj.attr("check") ? JSON.parse($obj.attr("check")) : false;
	var chooseParent = $obj.attr("chooseParent") ? JSON.parse($obj.attr("chooseParent")) : false;
	var idArr = $obj.find("input[name='" + name + "']").val().split(",");
	var textName = name.replace(".id", "") + ".name";
	var nameArr = $obj.find("input[name='" + textName + "']").val().split(",");
	var type = $obj.attr("type") ? $obj.attr("type") : "post";
	
	var callbackFuc = $obj.attr("callback");
	
	layui.layer.open({
		type: 1,
		id: 'LAY-popup-treeSelect',
		url: 'admin/tag/treeSelect',
		title: layui.admin.lang() ? 'treeSelect' : '请选择',
		area: ['300px', '500px'],
		shadeClose: true,
		closeBtn: false,
		btn: layui.admin.lang() ? ['ok', 'close'] : ['确定', '取消'],
		success: function(layero, index) {
			var _this = this;
			layui.view(_this.id).render(_this.url, typeof _this.initData === 'function' ? _this.initData.call(_this) : _this.initData || null).done(function() {
				if(actionUrl) {
					var setting = {
						data: {
							simpleData: {
								enable: true,
								idKey: "id",
								pIdKey: "parentId"
							},
							key: {
								name: "name" + layui.admin.lang()
							}
						}
					};
					if(check) {
						setting.check = {
							enable: true,
							chkStyle: "checkbox",
							chkboxType: {
								"Y": "s",
								"N": "s"
							}
						}
					}
					if(sync && !check) {
						ztreeSyncUrl = actionUrl.split("?")[0];
						setting.async = {
							enable: true,
							url: getZtreeSyncUrl,
							contentType: "application/json",
							type: 'post',
							data: JSON.stringify({}),
							headers: {
								token: localStorage.getItem('$tokenBPM')
							},
							dataFilter: function(treeId, parentNode, responseData) {
								if(responseData && responseData.data) {
									$.each(responseData.data.data, function(i, obj) {
										if(obj.hasChildren == true) {
											obj.isParent = true
										}
									});
									return responseData.data.data
								}
								return ""
							}
						};
						setting.callback = {
							onClick: function(event, treeId, treeNode) {},
							onAsyncSuccess: function(event, treeId, treeNode, msg) {
								var zTreeObj = $.fn.zTree.getZTreeObj("treeSelectTag");
								for(var i = 0; i < idArr.length; i += 1) {
									var node = zTreeObj.getNodeByParam("id", idArr[i]);
									try {
										zTreeObj.checkNode(node, true, false);
										zTreeObj.selectNode(node, false);
										zTreeObj.expandNode(node, true, false, false)
									} catch(e) {}
								}
							}
						};
						LayerUtil.ajax({
							url: layui.admin.basePath + actionUrl,
							data: JSON.stringify({}),
							type: type,
							success: function(res) {
								$.each(res.data.data, function(i, obj) {
									if(obj.hasChildren == true) {
										obj.isParent = true
									}
								});
								var zTreeObj = $.fn.zTree.init($(layero).find("#treeSelectTag"), setting, res.data.data);
								var nodes = zTreeObj.getNodesByParam("level", 0);
								for(var i = 0; i < nodes.length; i += 1) {
									zTreeObj.expandNode(nodes[i], true, false, false)
								}
							}
						})
					} else {
						LayerUtil.ajax({
							url: layui.admin.basePath + actionUrl,
							type: type,
							data: JSON.stringify({}),
							success: function(res) {
								var zTreeObj = $.fn.zTree.init($(layero).find("#treeSelectTag"), setting, res.data.data);
								var nodes = zTreeObj.getNodesByParam("level", 0);
								for(var i = 0; i < nodes.length; i += 1) {
									zTreeObj.expandNode(nodes[i], true, false, false)
								}
								for(var i = 0; i < idArr.length; i += 1) {
									var node = zTreeObj.getNodeByParam("id", idArr[i]);
									try {
										zTreeObj.checkNode(node, true, false);
										zTreeObj.selectNode(node, false)
									} catch(e) {}
								}
							}
						})
					}
				}
			})
		},
		end: function() {
			typeof end === 'function' && end.apply(this, arguments)
		},
		maxmin: 1,
		closeBtn: 1,
		yes: function(index, layero) {
			var idStr = [],
				nameStr = [];
			var zTreeObj = $.fn.zTree.getZTreeObj("treeSelectTag");
			if(check) {
				var nodes = zTreeObj.getCheckedNodes(true);
				if(nodes.length == 0) {
					LayerUtil.warning(layui.admin.lang() ? "At least choose one!" : "请至少选择一条数据");
					return false
				}
				$.each(nodes, function(idx, item) {
					if(!item.isParent) {
						idStr.push(item.id);
						nameStr.push(item.name)
					}
				});
				idStr = idStr.join(",");
				nameStr = nameStr.join(",")
			} else if(!check && !chooseParent) {
				var nodes = zTreeObj.getSelectedNodes();
				if(nodes.length == 0) {
					LayerUtil.warning(layui.admin.lang() ? "At least choose one!" : "请至少选择一条数据");
					return false
				}
				$.each(nodes, function(idx, item) {
					if(!item.isParent) {
						idStr.push(item.id);
						nameStr.push(item.name)
					}
				});
				idStr = idStr.join(",");
				nameStr = nameStr.join(",");
				if(!idStr || !nameStr) {
					LayerUtil.warning(layui.admin.lang() ? "Choose root node!" : "请选择根节点");
					return false
				}
			} else if(!check && chooseParent) {
				var nodes = zTreeObj.getSelectedNodes(true);
				if(nodes.length == 0) {
					LayerUtil.warning(layui.admin.lang() ? "At least choose one!" : "请至少选择一条数据");
					return false
				}
				$.each(nodes, function(idx, item) {
					idStr.push(item.id);
					nameStr.push(item.name)
				});
				idStr = idStr.join(",");
				nameStr = nameStr.join(",")
			}
			$obj.find("input[name='" + name + "']").val(idStr);
			$obj.find("input[name='" + textName + "']").val(nameStr);
			ValidateUtil.closeTooltip($obj.find("input[name='" + textName + "']"));
			
			if(typeof window[callbackFuc] == "function"){
		     	window[callbackFuc](nodes, $obj);
		    }
			
			layui.layer.close(index)
		},
		cancel: function(index) {}
	})
});
$(document).on("click", "div[component-type='treeSelectSetValue'] .tag-div-button", function() {
	var _this = this;
	var $obj = $(_this).closest("div[component-type='treeSelectSetValue']");
	var name = $obj.attr("name");
	var actionUrl = $obj.attr("actionUrl");
	var idArr = $obj.find("input[name='" + name + "']").val().split(",");
	var textName = name.replace(".id", "") + ".name";
	var nameArr = $obj.find("input[name='" + textName + "']").val().split(",");
	var type = $obj.attr("type") ? $obj.attr("type") : "post";
	var check = $obj.attr("check") ? JSON.parse($obj.attr("check")) : false;
	
	var callbackFuc = $obj.attr("callback");
	
	layui.layer.open({
		type: 1,
		id: 'LAY-popup-treeSelect',
		url: 'admin/tag/treeSelect',
		title: layui.admin.lang() ? 'Choose People' : '请选择',
		area: ['300px', '500px'],
		shadeClose: true,
		closeBtn: false,
		btn: layui.admin.lang() ? ['ok', 'close'] : ['确定', '取消'],
		success: function(layero, index) {
			var _this = this;
			layui.view(_this.id).render(_this.url, typeof _this.initData === 'function' ? _this.initData.call(_this) : _this.initData || null).done(function() {
				if(actionUrl) {
					var setting = {
						data: {
							simpleData: {
								enable: true,
								idKey: "id",
								pIdKey: "parentId"
							},
							key: {
								name: "name" + layui.admin.lang()
							}
						}
					};
					if(check) {
						setting.check = {
							enable: true,
							chkStyle: "checkbox",
							chkboxType: {
								"Y": "s",
								"N": "s"
							}
						}
					}
					LayerUtil.ajax({
						url: layui.admin.basePath + actionUrl,
						type: type,
						data: JSON.stringify({}),
						success: function(res) {
							var zTreeObj = $.fn.zTree.init($(layero).find("#treeSelectTag"), setting, res.data.data);
							var nodes = zTreeObj.getNodesByParam("level", 0);
							for(var i = 0; i < nodes.length; i += 1) {
								zTreeObj.expandNode(nodes[i], true, false, false)
							}
							for(var i = 0; i < idArr.length; i += 1) {
								var node = zTreeObj.getNodeByParam("loginName", idArr[i]);
								try {
									zTreeObj.checkNode(node, true, false);
									zTreeObj.selectNode(node, false)
								} catch(e) {}
							}
						}
					})
				}
			})
		},
		end: function() {
			typeof end === 'function' && end.apply(this, arguments)
		},
		maxmin: 1,
		closeBtn: 1,
		yes: function(index, layero) {
			var idStr = [],
				nameStr = [];
			var zTreeObj = $.fn.zTree.getZTreeObj("treeSelectTag");
			var nodes = "";
			if(check) {
				nodes = zTreeObj.getCheckedNodes(true);
				if(nodes.length == 0) {
					LayerUtil.warning(layui.admin.lang() ? "At least choose one!" : "请至少选择一条数据");
					return false
				}
				$.each(nodes, function(idx, item) {
					if(!item.isParent) {
						idStr.push(item.loginName);
						nameStr.push(item.name)
					}
				});
				idStr = idStr.join(",");
				nameStr = nameStr.join(",")
			} else {
				nodes = zTreeObj.getSelectedNodes();
				if(nodes.length == 0) {
					LayerUtil.warning(layui.admin.lang() ? "At least choose one!" : "请至少选择一条数据");
					return false
				}
				$.each(nodes, function(idx, item) {
					if(!item.isParent) {
						idStr.push(item.loginName);
						nameStr.push(item.name)
					}
				});
				idStr = idStr.join(",");
				nameStr = nameStr.join(",");
				if(!idStr || !nameStr) {
					LayerUtil.warning(layui.admin.lang() ? "Choose root node!" : "请选择根节点");
					return false
				}
			}
			$obj.find("input[name='" + name + "']").val(idStr);
			$obj.find("input[name='" + textName + "']").val(nameStr);
			ValidateUtil.closeTooltip($obj.find("input[name='" + textName + "']"));
			
			if(typeof window[callbackFuc] == "function"){
     			window[callbackFuc](nodes, $obj);
    		}
			
			layui.layer.close(index)
		},
		cancel: function(index) {}
	})
});
$(document).on("click", "div[component-type='iconSelect'] .tag-div-button", function() {
	var _this = this;
	var $obj = $(_this).closest("div[component-type='iconSelect']");
	if($obj.attr("disabled")) {
		return false
	}
	var name = $obj.attr("name");
	var value = $obj.find("input[name='" + name + "']").val();
	
	var callbackFuc = $obj.attr("callback");
	
	layui.layer.open({
		type: 1,
		id: 'LAY-popup-iconSelect',
		url: 'admin/tag/iconSelect',
		title: layui.admin.lang() ? 'iconSelect' : '图标选择框',
		area: ['700px', '500px'],
		shadeClose: true,
		closeBtn: false,
		btn: layui.admin.lang() ? ['ok', 'close'] : ['确定', '取消'],
		success: function(layero, index) {
			var _this = this;
			layui.view(_this.id).render(_this.url, typeof _this.initData === 'function' ? _this.initData.call(_this) : _this.initData || null).done(function() {
				$(layero).find("#iconSelectTag .the-icons li.fa-hover").on("click", function() {
					$(layero).find("#iconSelectTag .the-icons li.fa-hover").removeClass("active");
					$(this).addClass("active")
				});
				if(value) {
					$(layero).find("#iconSelectTag .the-icons li.fa-hover i." + value).parent().addClass("active")
				}
			})
		},
		end: function() {
			typeof end === 'function' && end.apply(this, arguments)
		},
		maxmin: 1,
		closeBtn: 1,
		yes: function(index, layero) {
			if($(layero).find("#iconSelectTag .the-icons li.fa-hover.active").length > 0) {
				var icon = $(layero).find("#iconSelectTag .the-icons li.fa-hover.active").find("i").attr("class");
				icon = icon.replace("fa ", "");
				$obj.find("input[name='" + name + "']").val(icon);
				ValidateUtil.closeTooltip($obj.find("input[name='" + name + "']"));
				
				if(typeof window[callbackFuc] == "function"){
			     	window[callbackFuc](icon, $obj);
			    }
				
				layui.layer.close(index)
			} else {
				LayerUtil.warning(layui.admin.lang() ? "Please choose icon!" : "请选择图标")
			}
			return false
		},
		cancel: function(index) {}
	})
});
$(document).on("click", "div[component-type='grid'] .tag-div-button", function() {
	var _this = this;
	var $obj = $(_this).closest("div[component-type='grid']");
	if($obj.attr("disabled")) {
		return false
	}
	var tableName = $obj.attr("tableName");
	var searchKey = $obj.attr("searchKey");
	var fieldKeys = $obj.attr("fieldKeys");
	var fieldLabels = $obj.attr("fieldLabels");
	var searchLabel = $obj.attr("searchLabel");
	var javaField = $obj.attr("javaField");
	fieldKeys = fieldKeys.split(",");
	fieldLabels = fieldLabels.split(",");
	var name = $obj.attr("name");
	var idArr = $obj.find("input[name='" + name + "']").val().split(",");
	var textName = name.replace(".id", "") + "." + javaField.replace(name + "|", "");
	var nameArr = $obj.find("input[name='" + textName + "']").val().split(",");
	
	var isRadio =  $obj.attr("radio");
	var isUrl =  $obj.attr("url");
	var callbackFuc = $obj.attr("callback");
	
	layui.layer.open({
		type: 1,
		id: 'LAY-popup-iconSelect',
		url: 'admin/tag/grid',
		title: layui.admin.lang() ? 'iconSelect' : '请选择',
		area: ['700px', '500px'],
		shadeClose: true,
		closeBtn: false,
		btn: layui.admin.lang() ? ['ok', 'close'] : ['确定', '取消'],
		success: function(layero, index) {
			var _this = this;
			layui.view(_this.id).render(_this.url, typeof _this.initData === 'function' ? _this.initData.call(_this) : _this.initData || null).done(function() {
				$(layero).find("#searchLabel").text(searchLabel);
				$(layero).find("#searchBtn").on("click", function() {
					$(layero).find("#gridTag").bootstrapTable('refresh')
				});
				var columns = [{
					checkbox: true,
					formatter: function(value, row, index) {
						if(idArr.join(",").indexOf(row.id) > -1) {
							return {
								checked: true
							}
						};
						return value
					}
				}];
				$.each(fieldKeys, function(idx, item) {
					columns.push({
						field: item,
						title: fieldLabels[idx]
					})
				});
				var opt = {
					classes: 'table table-hover',
					url: layui.admin.basePath + '/dynamic/zform/gridselectData',
					method: 'post',
					dataType: "json",
					showRefresh: false,
					showToggle: false,
					showColumns: false,
					showExport: false,
					showPaginationSwitch: false,
					minimumCountColumns: 2,
					striped: true,
					cache: false,
					pagination: true,
					sortOrder: "asc",
					pageNumber: 1,
					pageSize: 10,
					pageList: [10, 25, 50, 100],
					sidePagination: "server",
					ajaxOptions: {
						headers: {
							token: localStorage.getItem('$tokenBPM')
						}
					},
					queryParams: function(param) {
						var data = {};
						data.pageParam = {};
						data.pageParam.pageNo = (param.offset / param.limit) + 1;
						data.pageParam.pageSize = param.limit;
						data.pageParam.orderBy = "";
						data.tableName = tableName;
						data.searchKey = searchKey;
						data.searchValue = $(layero).find("#searchValue").val();
						return data
					},
					columns: columns,
					onLoadSuccess: function(data) {
						if(data.token) {
							localStorage.setItem("$tokenBPM", data.token)
						}
						$(layero).find("#gridTag").bootstrapTable("load", data)
					}
				};
				if(isUrl){
					opt.url = layui.admin.basePath + isUrl;
				}
				$(layero).find("#gridTag").bootstrapTable(opt)
			})
		},
		end: function() {
			typeof end === 'function' && end.apply(this, arguments)
		},
		maxmin: 1,
		closeBtn: 1,
		yes: function(index, layero) {
			var ids = $.map($(layero).find("#gridTag").bootstrapTable('getSelections'), function(row) {
				return row.id
			});
			/*var nameStr = $.map($(layero).find("#gridTag").bootstrapTable('getSelections'), function(row) {
				return row[fieldKeys[0]]
			});*/
			var nameStr = $.map($(layero).find("#gridTag").bootstrapTable('getSelections'), function(row) {
				if(row[fieldKeys[0]]){
					return row[fieldKeys[0]]
				}else if(fieldKeys[0].split(".").length > 0){
					return row[fieldKeys[0].split(".")[0]][fieldKeys[0].split(".")[1]]
				}else{
					return row[fieldKeys[0]]
				}
			});
			var resJson = $(layero).find("#gridTag").bootstrapTable('getSelections');
			if(ids.length == 0) {
				LayerUtil.alert(layui.admin.lang() ? 'At least one' : '请至少选择一条数据');
				return false
			} else {
				if(isRadio){
					if(ids.length > 1) {
						LayerUtil.alert(layui.admin.lang() ? 'Only one data can be selected' : '只能选择一条数据');
						return false;
					}
				}
				$obj.find("input[name='" + name + "']").val(ids.join(","));
				$obj.find("input[name='" + textName + "']").val(nameStr.join(","));
				ValidateUtil.closeTooltip($obj.find("input[name='" + textName + "']"));
				
				//进入回调函数
				if(typeof window[callbackFuc] == "function"){
					window[callbackFuc](resJson, $obj);
				}
				
				layui.layer.close(index)
			}
		},
		cancel: function(index) {}
	})
});
$(document).on("change", "select[name='secLevel-single-file']", function() {
	var secLevel = $(this).val();
	var id = $(this).siblings(".webUploadUpload").eq(0).attr("data-fileid");
	LayerUtil.ajax({
		url: layui.admin.basePath + "/system/sysFile/saveSecretLevel",
		data: JSON.stringify({
			id: id,
			secretLevel: secLevel
		}),
		type: 'post',
		success: function(result){
			
		}
	});
});
$(document).on("change", "select[name='secLevel']", function() {
	var secLevel = parseInt($(this).val());
	if(!isNaN(secLevel)){
		var form = $(this).closest("#inputForm");
		form.find("select[name='secLevel-single-file']").each(function(i,item){
			var value = parseInt($(item).val());
			var secHtml = myHelper.getSecOptionHtml(secLevel);
			$(item).html(secHtml);
			if(secLevel < value){
				$(item).val(secLevel).trigger("change");
			}else{
				$(item).val(value);
			}
		})
	}
});