;
(function(global) {
	"use strict";
	var BootstrapTableUtil = function(getTime, isActivity, getTime2, trigger) {
		var _this = this;
		var tableGetTime = getTime2 ? getTime2 : getTime;
		_this.dom = $("#table-list" + getTime);
		_this.body = $("#body-div" + tableGetTime);
		_this.config = {
			classes: 'table table-hover',
			dataType: "json",
			method: 'GET',
			cache: false,
			pagination: true,
			pageNumber: 1,
			pageSize: 10,
			pageList: [10, 25, 50, 100],
			sidePagination: "server",
			uniqueId: "uniqueId",
			showRefresh: false,
			showToggle: false,
			showColumns: false,
			showExport: false,
			showPaginationSwitch: false,
			ajaxOptions: {
				headers: {
					token: localStorage.getItem('$tokenBPM')
				}
			},
			showSearch: false,
			sortOrder: "asc",
			queryParams: function(param) {
				var options = _this.dom.bootstrapTable('getOptions');
				if(options.columns) {
					var columns = options.columns[0]
				}
				var data = myHelper.composeData(_this.body.find("#searchForm"));
				if(isActivity) {
					data.pageNo = (param.offset / param.limit) + 1;
					data.pageSize = param.limit
				} else {
					if(!data.pageParam){
						data.pageParam = {};
						data.pageParam.orderBy = param.sort === undefined ? "" : param.sort + " " + param.order
					}else{
						if(param.sort){
							data.pageParam.orderBy = param.sort === undefined ? "" : param.sort + " " + param.order
						}else{
							if(!data.pageParam.orderBy){
								data.pageParam.orderBy = param.sort === undefined ? "" : param.sort + " " + param.order
							}
						}
					}
					data.pageParam.pageNo = (param.offset / param.limit) + 1;
					data.pageParam.pageSize = param.limit;
				}
				if(_this.body.find(".layout-tab>li>a.active").length > 0) {
					data.status = data.status ? data.status : _this.body.find(".layout-tab>li>a.active").attr("data-status")
				}
				return data
			},
			onLoadSuccess: function(data) {
				if(data.token) {
					localStorage.setItem("$tokenBPM", data.token)
				}
				$.each(data.rows, function(index, obj) {
					obj.uniqueId = index
				});
				_this.dom.bootstrapTable("load", data);
				if(_this.body.find(".layui-tab-content").length > 0 && trigger) {
					_this.body.find(".layui-tab-content .v-hidden").removeClass("v-hidden")
				}
			},
			onPostBody: function() {
				_this.body.find(".hide-btn-group").each(function(i, item) {
					var width = 0;
					_this.body.find(this).find(".btn").each(function(index, obj) {
						var count = $(obj).text().length;
						if(layui.admin.lang()) {
							width += count * 8 + 10
						} else {
							width += count * 15 + 10
						}
					});
					$(this).width(width)
				});
				var data = _this.dom.bootstrapTable('getData');
				$.each(data, function(i, item) {
					if(item.hasChildren === false) {
						_this.dom.find('tbody tr').eq(i).find(".detail-icon").hide()
					}
				})
			},
			onLoadError: function(data) {
				_this.body.find('.fixed-table-body table').bootstrapTable('removeAll')
			},
			onClickRow: function(row) {},
			userIcheck: true
		};
		_this.fieldColumns = [];
		_this.listDictionaryId = "";
		_this.saveUrl = "";
	};
	BootstrapTableUtil.prototype = {
		init: function(option, listDictionaryId, saveUrl) {
			this.bindFunction();
			this.appendToolBtns();
			this.setBtnLang();
			this.setSearchFormLang(listDictionaryId);
			
			var _this = this;
			_this.listDictionaryId = listDictionaryId;
			_this.saveUrl = saveUrl;
			var complexHeader = _this.initFieldColumns(option, listDictionaryId, saveUrl);
			
			var totalConfig = $.extend(true, _this.config, option);
			if(_this.fieldColumns.length > 0){
				if(complexHeader){
					totalConfig.columns.unshift(_this.fieldColumns);
				}else{
					totalConfig.columns = [_this.fieldColumns,totalConfig.columns];
				}
			}
			
			if(saveUrl && !totalConfig.fixedColumns){
				totalConfig.onClickRow = function(row, $element, field) {
					$element.on("ifClicked","input[type='checkbox'].required",function(){
						if($.trim($(this).val()).length > 0){
							$(this).closest("fieldset").siblings("div.tooltip").hide();
						}
					})
					if(_this.dom.attr("editTable") == "true"){
						var grepColumObj = $.grep(_this.fieldColumns, function(obj,idx){
							return obj.fieldName == field;
						})
						if(grepColumObj.length > 0){
							if(!$element.attr('editId') && _this.body.find("tr[editId]").length == 0){
								$element.attr('editId', row.id);
								$.each(_this.fieldColumns, function(idx, columnObj ) {
									if(columnObj.componentName){
										var splitValue = columnObj.fieldName.split(".");
										myHelper.editTableRow(listDictionaryId, columnObj, $($element.find("td")[idx]), row[splitValue[0]] ? row[splitValue[0]].name ? row[splitValue[0]].name : "" : "", row[splitValue[0]] ? row[splitValue[0]].id ? row[splitValue[0]].id : "" : "");
									}else{
										myHelper.editTableRow(listDictionaryId, columnObj, $($element.find("td")[idx]), row[columnObj.fieldName] ? row[columnObj.fieldName] : "");
									}
								});
							}else if(!$element.attr('editId')){
								_this.editTableRowSave();
							}
						}
						else{
						}
					}
		        }
			}
			
			return _this.dom.bootstrapTable(totalConfig);
		},
		initSon: function(option, listDictionaryId, saveUrl) {
			
			var _this = this;
			_this.listDictionaryId = listDictionaryId;
			_this.saveUrl = saveUrl;
			var complexHeader = _this.initFieldColumns(option, listDictionaryId, saveUrl);
			
			var totalConfig = $.extend(true, _this.config, option);
			if(_this.fieldColumns.length > 0){
				if(complexHeader){
					totalConfig.columns.unshift(_this.fieldColumns);
				}else{
					totalConfig.columns = [_this.fieldColumns,totalConfig.columns];
				}
			}
			
			if(saveUrl && !totalConfig.fixedColumns){
				totalConfig.onClickRow = function(row, $element, field) {
					if(_this.dom.attr("editTable") == "true"){
						var grepColumObj = $.grep(_this.fieldColumns, function(obj,idx){
							return obj.fieldName == field;
						})
						if(grepColumObj.length > 0){
							if(!$element.attr('editId') && _this.body.find("tr[editId]").length == 0){
								$element.attr('editId', row.id);
								$.each(_this.fieldColumns, function(idx, columnObj ) {
									if(columnObj.componentName){
										var splitValue = columnObj.fieldName.split(".");
										myHelper.editTableRow(listDictionaryId, columnObj, $($element.find("td")[idx]), row[splitValue[0]] ? row[splitValue[0]].name ? row[splitValue[0]].name : "" : "", row[splitValue[0]] ? row[splitValue[0]].id ? row[splitValue[0]].id : "" : "");
									}else if(!$element.attr('editId')){
										myHelper.editTableRow(listDictionaryId, columnObj, $($element.find("td")[idx]), row[columnObj.fieldName] ? row[columnObj.fieldName] : "");
									}
								});
							}else{
								_this.editTableRowSave();
							}
						}
						else{
						}
					}
		        }
			}
			
			return _this.dom.bootstrapTable(totalConfig);
		},
		initProc: function(option, listDictionaryId, procTable, saveUrl) {
			if(procTable == "unsent") {
				this.appendProcToolBtns(listDictionaryId)
				this.setSearchFormLang(listDictionaryId);
			} else if(procTable == "done") {
				var html = "";
				if(layui.admin.lang()) {
					html += '<button class="btn btn-sm btn-danger cancelBtn"><i class="fa fa-times" aria-hidden="true"></i> cancel</button><button class="btn btn-sm btn-danger backBtn"><i class="fa fa-reply" aria-hidden="true"></i> cancel</button>'
				} else {
					html += '<button class="btn btn-sm btn-danger cancelBtn"><i class="fa fa-times" aria-hidden="true"></i> 撤销</button><button class="btn btn-sm btn-danger backBtn"><i class="fa fa-reply" aria-hidden="true"></i> 取回</button>'
				}
				this.dom.closest("div").find(".proc-btn-div").append(html)
			} else if(procTable == "todoAndDoing"){
				var html = "";
				var roles = localStorage.getItem('roles');
				if(roles && roles.indexOf("dleader") != -1  ){
					if(layui.admin.lang()) {
						html += '<button class="btn btn-sm btn-primary cancelBtn"> 批量审批</button>'
					} else {
						html += '<button class="btn btn-sm btn-primary cancelBtn"> 批量审批</button>'
					}
				}
				this.dom.closest("div").find(".proc-btn-div").append(html)
			}
			this.bindFunction();
			this.setBtnLang();
			this.body.find("#Unsent").text(layui.admin.lang() ? "Unsent" : "待发");
			this.body.find("#TodoAndDoing").text(layui.admin.lang() ? "Todo" : "待办");
			this.body.find("#Done").text(layui.admin.lang() ? "Done" : "已办");
			
			var _this = this;
			_this.listDictionaryId = listDictionaryId;
			_this.saveUrl = saveUrl;
			var complexHeader = _this.initFieldColumns(option, listDictionaryId, saveUrl);
			
			var totalConfig = $.extend(true, _this.config, option);
			if(_this.fieldColumns.length > 0){
				if(complexHeader){
					totalConfig.columns.unshift(_this.fieldColumns);
				}else{
					totalConfig.columns = [_this.fieldColumns,totalConfig.columns];
				}
			}
			
			return _this.dom.bootstrapTable(totalConfig);
		},
		appendProcToolBtns: function(listDictionaryId) {
			var _this = this;
			LayerUtil.ajax({
				type: "get",
				url: layui.admin.basePath + '/dynamic/zform/getProcDefList?formNo=' + listDictionaryId,
				async: false,
				success: function(res) {
					var html = "";
					$.each(res.data.procDefList, function(i, obj) {
						html += '<button id="addBtn" class="btn btn-sm btn-success addBtn" data-procDefKey="' + obj.procDefKey + '"><i class="fa fa-plus"></i> ' + obj.procDefName + '</button>'
					});
					_this.dom.closest("div").find(".proc-btn-div").append(html)
				}
			})
		},
		setBtnLang: function() {
			var _this = this;
			if(layui.admin.lang()) {
				_this.body.find("#toolbar").find("#searchBtn font").text("Search");
				_this.body.find("#toolbar").find("#resetBtn font").text("Reset");
				_this.body.find("#toolbar").find("#easySearch font").text("Back");
				_this.body.find("#toolbar").find("#hardSearch font").text("Advanced Query");
				_this.body.find(".searchDiv.positionSpc").css("right", "300px")
			}
		},
		setSearchFormLang: function(dictionaryId) {
			var _this = this;
			myHelper.setFormLang(dictionaryId, _this.body.find("#searchForm"))
		},
		appendToolBtns: function() {
			var _this = this;
			_this.body.find("#toolbar").prepend(LayerUtil.getBtnsStrFromMenuData().toolBtnsStr)
		},
		bindFunction: function() {
			var _this = this;
			_this.body.find("#searchBtn").on("click", function() {
				_this.refresh()
			});
			_this.body.find("#resetBtn").on("click", function() {
				_this.body.find("#searchForm input[type='text']").val("");
				_this.body.find("#searchForm input[type='hidden']").val("");
				_this.body.find("#searchForm select[component-type='select']").val("").trigger("change")
			})
		},
		refresh: function() {
			this.dom.bootstrapTable('refresh')
		},
		destroy: function() {
			this.dom.bootstrapTable('destroy');
		},
		concatColumns: function(columns, columnsAjax) {
			$.each(columns, function(index, item) {
				var arr = $.grep(columnsAjax, function(n, i) {
					return n.field == item.field
				});
				if(arr.length > 0) {
					columnsAjax = $.grep(columnsAjax, function(n, i) {
						return n.field != item.field
					})
				}
			});
			return columnsAjax.concat(columns)
		},
		getSingleId: function() {
			var _this = this;
			var ids = _this.getIdSelections();
			if(ids.length != 1) {
				LayerUtil.alert(layui.admin.lang() ? 'only one' : '请选择一条数据');
				return false
			}
			return ids[0]
		},
		getMultiId: function() {
			var _this = this;
			var ids = _this.getIdSelections();
			if(ids.length == 0) {
				LayerUtil.alert(layui.admin.lang() ? 'at least one' : '请至少选择一条数据');
				return false
			}
			return ids
		},
		getIdSelections: function() {
			var _this = this;
			var ids = [];
			_this.body.find("table.table").each(function(i, obj) {
				if(!obj.closest(".fixed-columns") && !obj.closest(".fixed-columns-right")) {
					var checked = $.map($(obj).bootstrapTable('getSelections'), function(row) {
						return row.id
					});
					if(checked.length > 0) {
						ids = ids.concat(checked)
					}
				}
			});
			return ids;
		},
		getSingleRow: function() {
			var _this = this;
			var ids = _this.getRowSelections();
			if(ids.length != 1) {
				LayerUtil.alert(layui.admin.lang() ? 'only one' : '请选择一条数据');
				return false
			}
			return ids[0]
		},
		getMultiRow: function() {
			var _this = this;
			var ids = _this.getRowSelections();
			if(ids.length == 0) {
				LayerUtil.alert(layui.admin.lang() ? 'at least one' : '请至少选择一条数据');
				return false
			}
			return ids
		},
		getRowSelections: function() {
			var _this = this;
			var ids = [];
			_this.body.find("table.table").each(function(i, obj) {
				if(!obj.closest(".fixed-columns") && !obj.closest(".fixed-columns-right")) {
					var checked = $.map($(obj).bootstrapTable('getSelections'), function(row) {
						return row
					});
					if(checked.length > 0) {
						ids = ids.concat(checked)
					}
				}
			});
			return ids;
		},
		initFieldColumns:function(option, listDictionaryId, saveUrl){
			var _this = this;
			var complexHeader = false;
			layui.admin.getDictionary('page-table-cell', listDictionaryId, function(data) {
				if(data && data.length > 0) {
					$.each(option.columns, function(idx, item) {
						if($.isPlainObject(item)) {
							var fieldColumnObj = {
								"class": 'thHide',
								fieldName: item.field,
								componentType: item.componentType,
								componentId: item.componentId,
								componentDateFmt: item.componentDateFmt,
								componentName: item.componentName,
								componentActionUrl: item.componentActionUrl,
								componentSync: item.componentSync,
								componentCheck: item.componentCheck,
								componentChooseParent: item.componentChooseParent,
								tableName: item.tableName,
								searchKey: item.searchKey,
								fieldKeys: item.fieldKeys,
								fieldLabels: item.fieldLabels,
								searchLabel: item.searchLabel,
								javaField: item.javaField,
								formatter: item.formatter,
								componentRequired: item.componentRequired,
								componentDisabled: item.componentDisabled
							};
							if(item.field) {
								var find = layui.admin.filterList(data, {
									member: item.field
								})[0];
								if(find) {
									item.title = find['memberName' + layui.admin.lang()] || item.title
								}
								if(option.classes && option.classes.indexOf("table-fiexd") > -1){
									if(!item.width) {
										fieldColumnObj.width = "150px";
									}else{
										fieldColumnObj.width = item.width;
									}
								}
								_this.fieldColumns.push(fieldColumnObj);
							}else{
								if(item['class'] && item['class'].indexOf("must") > -1){
									_this.fieldColumns.push({
										"class": 'thHide',
										fieldName: "must",
										width: '45px'
									});
								}else if(item.checkbox){
									_this.fieldColumns.push({
										"class": 'thHide',
										fieldName: "checkbox",
										width: '36px'
									});
								}
							}
						}else if(Array.isArray(item)){
							complexHeader = true;
							$.each(item, function(idx2, item2) {
								if($.isPlainObject(item2)) {
									var fieldColumnObj = {
										"class": 'thHide',
										fieldName: item2.field,
										componentType: item2.componentType,
										componentId: item2.componentId,
										componentDateFmt: item2.componentDateFmt,
										componentName: item2.componentName,
										componentActionUrl: item2.componentActionUrl,
										componentSync: item2.componentSync,
										componentCheck: item2.componentCheck,
										componentChooseParent: item2.componentChooseParent,
										tableName: item2.tableName,
										searchKey: item2.searchKey,
										fieldKeys: item2.fieldKeys,
										fieldLabels: item2.fieldLabels,
										searchLabel: item2.searchLabel,
										javaField: item2.javaField,
										formatter: item2.formatter,
										componentRequired: item2.componentRequired,
										componentDisabled: item2.componentDisabled
									};
									if(item2.field) {
										var find = layui.admin.filterList(data, {
											member: item2.field
										})[0];
										if(find) {
											item2.title = find['memberName' + layui.admin.lang()] || item2.title
										}
										if(option.classes && option.classes.indexOf("table-fiexd") > -1){
											if(!item2.width) {
												fieldColumnObj.width = "150px";
											}else{
												fieldColumnObj.width = item2.width;
											}
										}
										_this.fieldColumns.push(fieldColumnObj);
									}else{
										if(item2['class'] && item2['class'].indexOf("must") > -1){
											_this.fieldColumns.push({
												"class": 'thHide',
												width: "45px",
												fieldName: "must"
											});
										}else if(item2.checkbox){
											_this.fieldColumns.push({
												"class": 'thHide',
												width: "36px",
												fieldName: "checkbox"
											});
										}
									}
								}
							})
						}
					})
				}
			});
			return complexHeader;
		},
		editTableRowSave:function(){
			var _this = this;
			var continueStatus = true;
			if(_this.dom.find("tr[editid]").length > 0){
				var validateForm = _this.body.find("#editTableForm").validate({});
            	if(validateForm.form()){
	            	var data = myHelper.composeData(_this.body.find("#editTableForm"));
		        	if(_this.body.find("tr[editId]").attr("editId") != "addRowId"){
		        		data.id = _this.body.find("tr[editId]").attr("editId");
		        	}
		        	data.formNo = _this.listDictionaryId;
		        	LayerUtil.ajax({
						url : layui.admin.basePath + _this.saveUrl,
						type : 'post',
						data : JSON.stringify(data),
						shade: true,
						async: false,
						success : function(res) {
							LayerUtil.success(res[LayerUtil.getMsgLang()]);
							
						   	var rowData = data;
						   	delete rowData.id;
						   	delete rowData.formNo;
						   	$.each(rowData, function(itemKey, itemValue) {
						   		if(/^c((0[1-9]{1})|(10))List$/.test(itemKey)) {
						   			rowData[itemKey.replace("List","")] = itemValue.join(",");
						   		}
						   		else if(/^g(0[1-5]{1})$/.test(itemKey)){
						   			$.each(itemValue, function(itemValueKey,itemValueValue) {
						   				if(itemValueKey != "id"){
						   					rowData[itemKey].name = itemValueValue;
						   				}
						   			});
						   		}
						   	});
						   	_this.dom.bootstrapTable('updateRow', {
							    index: _this.dom.find("tr[editid]").index(),
							    row: rowData
						    });
							_this.body.find("tr[editId]").removeAttr("editId");
							/*console.log(rowData)
							var allTableData = _this.dom.bootstrapTable('getData');//获取表格的所有内容行
						   	console.log(allTableData);
							return false;*/
							
							/*$.each(_this.fieldColumns, function(idx, columnObj ) {
								if(columnObj.fieldName != "checkbox" && columnObj.fieldName != "must"){
									var $td = $(_this.body.find("tr[editId]").find("td")[idx]);
									if(columnObj.componentName){
										var splitValue = columnObj.fieldName.split(".");
										
										if(columnObj.javaField){
											var splitJavaField = columnObj.javaField.split("|");
											if(typeof columnObj.formatter == "function"){
												if(data[splitValue[0]].id){
													$td.html(columnObj.formatter(data[splitValue[0]].id));
												}else{
													$td.html("");
												}
											}else{
												if(data[splitValue[0]][splitJavaField[1]]){
													$td.html(data[splitValue[0]][splitJavaField[1]]);
												}else{
													$td.html("");
												}
											}
										}else{
											if(typeof columnObj.formatter == "function"){
												$td.html(columnObj.formatter(data[splitValue[0]].id));
											}else{
												$td.html(data[splitValue[0]].name);
											}
										}
									}else{
										if(typeof columnObj.formatter == "function"){
											if(data[columnObj.fieldName]){
												$td.html(columnObj.formatter(data[columnObj.fieldName]));
											}
											else if(Array.isArray(data[columnObj.fieldName+"List"])){
												$td.html(columnObj.formatter(data[columnObj.fieldName+"List"].join(",")));
											}
											else{
												$td.html("");
											}
										}else{
											if(data[columnObj.fieldName]){
												$td.html(data[columnObj.fieldName]);
											}
											else{
												$td.html("");
											}
										}
									}
								}
							});
							_this.body.find("tr[editId]").removeAttr("editId");*/
						}
					});
            	}else{
            		continueStatus = false;
            	}
			}
			return continueStatus;
		},
	};
	if(typeof module !== 'undefined' && module.exports) {
		module.exports = BootstrapTableUtil
	}
	if(typeof define === 'function') {
		define(function() {
			return BootstrapTableUtil
		})
	}
	global.BootstrapTableUtil = BootstrapTableUtil;
})(this);