var LayerUtil = {
	init: function(option, dictionary) {},
	setInputFormLang: function(dictionaryId, layero) {
		var _this = this;
		myHelper.setFormLang(dictionaryId, $($(layero).find("#inputForm")))
	},
	openDialogFlow: function(options) {
		var _this = this;
		var success = options.success,
			skin = options.skin;
		delete options.success;
		delete options.skin;
		var func = options.func;
		var btn = [];
		$.each(options.initData.paramStr, function(i, obj) {
			if(layui.admin.lang()) {
				btn.push(obj.button_EN)
			} else {
				btn.push(obj.button)
			}
		});
		var opt = {
			title: options.title,
			formLay: options.formLay,
			initData: options.initData,
			url: options.url,
			area: [options.width || '900px', options.height || '500px'],
			offset: $(".wy-layer").length > 0 ? [$(".wy-layer").last().css("top").replace("px", "") * 1 + 10 + "px", $(".wy-layer").last().css("left").replace("px", "") * 1 + 10 + "px"] : 'auto',
			id: options.id ? options.id : 'LAY-popup-' + options.formLay,
			shadeClose: false,
			skin: options.full ? '' : 'wy-layer',
			btn: btn,
			success: function(layero, index) {
				layui.view(this.id).render(options.url, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null).done(function() {
					typeof options.formInit === 'function' && options.formInit.call(this, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null);
					$(layero).find("#act-modal").load(layui.setter.views + 'admin/tag/actModal' + layui.setter.engine, function() {
						if(layui.admin.lang()) {
							$(layero).find("#act-modal").find("h4").each(function(i, item) {
								if($(item).text() == "选择分支") {
									$(item).text("Selection branch")
								} else if($(item).text() == "选择节点") {
									$(item).text("Selection node")
								} else if($(item).text() == "选择用户") {
									$(item).text("Selection user")
								} else if($(item).text() == "节点名称") {
									$(item).text("Node name")
								} else if($(item).text() == "选择流程") {
									$(item).text("Selection process")
								}
							});
							$(layero).find("#act-modal").find("#chooseUserUl li").each(function(i, item) {
								if($(item).text() == "推荐用户") {
									$(item).text("Recommended users")
								} else if($(item).text() == "全部用户") {
									$(item).text("All users")
								}
							});
							$(layero).find("#act-modal").find("button").each(function(i, item) {
								if($(item).text() == "提交") {
									$(item).text("Submit")
								}
							})
						}
					});
					if(options.actionUrl) {
						LayerUtil.ajax({
							url: options.actionUrl,
							data: options.actionType == "post" ? $.isPlainObject(options.initData) ? JSON.stringify(options.initData) : "" : "",
							type: options.actionType ? options.actionType : 'get',
							success: function(res) {
								var actionDataName = options.actionDataName ? options.actionDataName : "";
								var data = res.data;
								if(res.data && res.data[actionDataName]) {
									data = res.data[actionDataName]
								}
								if(options.formLay) {
									ProcUtil.init($(layero), index, options.formLay, data.ruleArgsJson);
									_this.setInputFormLang(options.formLay, layero);
									$(layero).find("#formNo").val(options.formLay)
								}
								ValidateUtil.init($(layero));
								$.each(data, function(k, value) {
									if($.isPlainObject(value)) {
										$.each(value, function(vk, vv) {
											var $obj = $(layero).find("form").find("[name='" + k + "." + vk + "']");
											var componentType = $obj.attr("component-type");
											switch(componentType) {
												case "select":
													$obj.val(vv).trigger("change");
													break;
												case "checkbox":
													var splitData = vv.split(",");
													$.each(splitData, function(i, v) {
														$(layero).find("form").find("[name='" + k + "." + vk + "List'][value='" + v + "']").iCheck('check')
													});
													break;
												case "radio":
													$(layero).find("form").find("[name='" + k + "." + vk + "'][value='" + vv + "']").iCheck('check');
													break;
												case "grid":
													var name = $obj.attr("name");
													var javaField = $obj.attr("javaField");
													var textName = javaField.replace(name + "|", "");
													$obj.val(vv);
													$(layero).find("form").find("[name='" + javaField.replace("id|", "") + "']").val(value.name);
													break;
												case "upload":
													break;
												case "input":
													$obj.val(vv);
													break;
												case "textarea":
													$obj.val(vv);
													break;
												case "datePicker":
													$obj.val(vv);
													break;
												case "treeSelect":
													var name = $obj.attr("name");
													$obj.val(vv);
													$(layero).find("form").find("[name='" + name.replace(".id", ".name") + "']").val(value.name);
													break;
												case "iconSelect":
													$obj.val(vv);
													break;
												case "summernote":
													$obj.summernote("code",vv);
												default:
													break
											}
										})
									} else {
										var $obj = $(layero).find("form").find("[name='" + k + "']");
										var componentType = $obj.attr("component-type");
										switch(componentType) {
											case "select":
												$obj.val(value).trigger("change");
												break;
											case "checkbox":
												var splitData = value.split(",");
												$.each(splitData, function(i, v) {
													$(layero).find("form").find("[name='" + k + "List'][value='" + v + "']").iCheck('check')
												});
												break;
											case "radio":
												$(layero).find("form").find("[name='" + k + "'][value='" + value + "']").iCheck('check');
												break;
											case "grid":
												break;
											case "upload":
												$(layero).find("form").find("input[name='" + k + "']").val(value);
												var componentAccept = $obj.attr("component-accept");
												var disabled = $obj.attr("disabled");
												if(value) {
													LayerUtil.ajax({
														url: layui.admin.basePath + '/system/sysFile/getFileList?groupId=' + value,
														async: false,
														type: 'post',
														success: function(result) {
															var data = result.data.fileListMap.files;
															var html = "";
															if(data) {
																for(var i = 0; i < data.length; i += 1) {
																	html += "<div>";
																	html += data[i].name;
																	html += "&emsp;";
																	if(componentAccept=="pic"){
																		html += "<img src='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' style='max-width:150px;'>"
																	}
																	if(layui.admin.lang()) {
																		html += "<a class='webUploadUpload' data-fileId='" + data[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' download='" + data[i].name + "' target='_blank'>Download</a>";
																		html += "&nbsp;";
																		if(disabled != "disabled"){
																			html += "<a class='webUploadRemove' data-fileId='" + data[i].id + "'>Delete</a>"
																		}
																	} else {
																		html += "<a class='webUploadUpload' data-fileId='" + data[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' download='" + data[i].name + "' target='_blank'>下载</a>";
																		html += "&nbsp;";
																		if(disabled != "disabled"){
																			html += "<a class='webUploadRemove' data-fileId='" + data[i].id + "'>删除</a>"
																		}
																	}
																	html += "</div>";
																}
															}
															$(layero).find("form").find("div[name='" + k + "']").find(".fileListDiv").html(html)
														}
													})
												}
												break;
											case "uploadsec":
												$(layero).find("form").find("input[name='" + k + "']").val(value);
												var componentAccept = $obj.attr("component-accept");
												var disabled = $obj.attr("disabled");
												if(value) {
													LayerUtil.ajax({
														url: layui.admin.basePath + '/system/sysFile/getFileList?groupId=' + value,
														async: false,
														type: 'post',
														success: function(result) {
															var dataList = result.data.fileListMap.files;
															var html = "";
															if(dataList) {
																for(var i = 0; i < dataList.length; i++) {
																	if(disabled == "disabled"){
																		html += "<div>";
																		var secValue = "";
																		layui.admin.getDictionary('data-params', 'sys_sec_level', function(res) {
																			$.each(res, function(index, term) {
																				if(term.member == dataList[i].secretLevel) {
																					secValue = term["memberName" + layui.admin.lang()];
																				}
																			})
																		})
																		if(!secValue){
																			secValue = "公开";
																		}
																		html += "<span style='margin-right:20px;line-height:25px;display:inline-block;width:60px;'>"+secValue+"</span>";
																		html += dataList[i].name;
																		html += "&emsp;";
																	}else{
																		html += "<div style='margin-top:10px;'>";
																		html += '<select name="secLevel-single-file" class="form-control-sec">';
																		html +=  myHelper.getSecOptionHtml(data.secLevel,dataList[i].secretLevel);
																		html +=  '</select>';
																		html += dataList[i].name;
																		html += "&emsp;";
																	}
																	if(componentAccept=="pic"){
																		html += "<img src='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + dataList[i].id + "' style='max-width:150px;'>"
																	}
																	if(layui.admin.lang()) {
																		html += "<a class='webUploadUpload' data-fileId='" + dataList[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?secret=1&fileId=" + dataList[i].id + "' download='" + dataList[i].name + "' target='_blank'>Download</a>";
																		html += "&nbsp;";
																		if(disabled != "disabled"){
																			html += "<a class='webUploadRemove' data-fileId='" + dataList[i].id + "'>Delete</a>"
																		}
																	} else {
																		html += "<a class='webUploadUpload' data-fileId='" + dataList[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?secret=1&fileId=" + dataList[i].id + "' download='" + dataList[i].name + "' target='_blank'>下载</a>";
																		html += "&nbsp;";
																		if(disabled != "disabled"){
																			html += "<a class='webUploadRemove' data-fileId='" + dataList[i].id + "'>删除</a>"
																		}
																	}
																	html += "</div>";
																}
															}
															$(layero).find("form").find("div[name='" + k + "']").find(".fileListDiv").html(html)
														}
													})
												}
												break;
											case "input":
												$obj.val(value);
												break;
											case "textarea":
												$obj.val(value);
												break;
											case "datePicker":
												$obj.val(value);
												break;
											case "treeSelect":
												break;
											case "iconSelect":
												$obj.val(value);
												break;
											case "summernote":
												$obj.summernote("code",value);
											default:
												break
										}
									}
								});
								if(options.setDataWidthAjax) {
									if(options.initData) {
										$.each(options.initData, function(k, v) {
											if($.isPlainObject(v)) {
												$.each(v, function(vk, vv) {
													$(layero).find("[name='" + k + "." + vk + "']").val(vv)
												})
											} else {
												$(layero).find("[name='" + k + "']").val(v)
											}
										})
									}
									if(typeof window.formFuction == "function") {
										window.formFuction(res, layero, options.formFunctionData);
										delete window.formFuction
									}
								}
								if(data.procInsId && $(layero).find("#procImgDiv").length > 0) {
									ProcUtil.drawProcPic($(layero).find("#procImgDiv"), data)
								}
								if(data.procInsId && $(layero).find("#procLogDiv").length > 0) {
									ProcUtil.drawProcLog($(layero).find("#procLogDiv"), data)
								}
								if($(layero).find("#requeridFormTab").length > 0) {
									$(layero).find("#requeridFormTab").text(layui.admin.lang() ? "Form" : "文单")
								}
								if($(layero).find("#procPicTab").length > 0) {
									$(layero).find("#procPicTab").text(layui.admin.lang() ? "Process chart" : "流程图")
								}
								if($(layero).find("#procLogTab").length > 0) {
									$(layero).find("#procLogTab").text(layui.admin.lang() ? "Process log" : "流程日志")
								}
								$(layero).on("click", "#showProcImgDiv", function() {
									$(layero).find("#procImgDiv").toggle();
									$(layero).find("#procLogDiv").hide()
								});
								$(layero).on("click", "#showProcLogDiv", function() {
									$(layero).find("#procLogDiv").toggle();
									$(layero).find("#procImgDiv").hide()
								})
							}
						})
					}
					if(options.full) {
						layui.layer.full(index)
					}
					if(options.setDataWithoutAjax) {
						if(options.initData) {
							$.each(options.initData, function(k, v) {
								if($.isPlainObject(v)) {
									$.each(v, function(vk, vv) {
										$(layero).find("[name='" + k + "." + vk + "']").val(vv)
									})
								} else {
									$(layero).find("[name='" + k + "']").val(v)
								}
							})
						}
						if(typeof window.formFuction == "function") {
							window.formFuction("", layero, options.formFunctionData);
							delete window.formFuction
						}
					}
				})
			},
			cancel: function(index) {},
			end: options.end
		};
		$.each(opt.btn, function(i, str) {
			if((i + 1) != opt.btn.length) {
				opt["btn" + (i + 1)] = function(index, layero) {
					func.call(this, index, layero, options.initData.paramStr[i], arguments);
					return false
				}
			}
		});
		return layui.layer.open($.extend({
			type: 1,
			title: layui.admin.lang() ? 'Tips' : '提示',
			content: '',
			id: 'LAY-system-view-popup',
			skin: 'layui-layer-admin' + (skin ? ' ' + skin : ''),
			shadeClose: true,
			closeBtn: false,
			success: function(layero, index) {
				var elemClose = $('<i class="layui-icon" close>&#x1006;</i>');
				layero.append(elemClose);
				elemClose.on('click', function() {
					layer.close(index)
				});
				typeof success === 'function' && success.apply(this, arguments)
			},
			end: function() {
				typeof end === 'function' && end.apply(this, arguments)
			},
			maxmin: typeof options.maxmin != "undefined" ? options.maxmin : options.full ? 0 : 1,
			closeBtn: 1
		}, opt))
	},
	openDialog: function(options) {
		var _this = this;
		var success = options.success,
			skin = options.skin;
		delete options.success;
		delete options.skin;
		var func1 = options.func1;
		var func2 = options.func2;
		var func3 = options.func3;
		var opt = {
			title: options.title,
			formLay: options.formLay,
			initData: options.initData,
			url: options.url,
			area: [options.width || '900px', options.height || '500px'],
			offset: $(".wy-layer").length > 0 ? [$(".wy-layer").last().css("top").replace("px", "") * 1 + 10 + "px", $(".wy-layer").last().css("left").replace("px", "") * 1 + 10 + "px"] : 'auto',
			id: options.id ? options.id : 'LAY-popup-' + options.formLay,
			shadeClose: false,
			skin: options.full ? '' : 'wy-layer',
			btn: layui.admin.lang() ? (options.btn_EN ? options.btn_EN : []) : (options.btn ? options.btn : []),
			success: function(layero, index) {
				layui.view(this.id).render(options.url, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null).done(function() {
					if(options.formLay) {
						_this.setInputFormLang(options.formLay, layero);
						$(layero).find("#formNo").val(options.formLay)
					}
					ValidateUtil.init($(layero));
					typeof options.formInit === 'function' && options.formInit.call(this, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null);
					if(options.actionUrl && options.initData) {
						LayerUtil.ajax({
							url: options.actionUrl,
							data: options.actionType == "post" ? $.isPlainObject(options.initData) ? JSON.stringify(options.initData) : "" : "",
							type: options.actionType ? options.actionType : 'get',
							success: function(res) {
								var actionDataName = options.actionDataName ? options.actionDataName : "";
								var data = res.data;
								if(res.data && res.data[actionDataName]) {
									data = res.data[actionDataName]
								}
								$.each(data, function(k, value) {
									if($.isPlainObject(value)) {
										$.each(value, function(vk, vv) {
											var $obj = $(layero).find("form").find("[name='" + k + "." + vk + "']");
											var componentType = $obj.attr("component-type");
											switch(componentType) {
												case "select":
													$obj.val(vv).trigger("change");
													break;
												case "checkbox":
													var splitData = vv.split(",");
													$.each(splitData, function(i, v) {
														$(layero).find("form").find("[name='" + k + "." + vk + "List'][value='" + v + "']").iCheck('check')
													});
													break;
												case "radio":
													$(layero).find("form").find("[name='" + k + "." + vk + "'][value='" + vv + "']").iCheck('check');
													break;
												case "grid":
													var name = $obj.attr("name");
													var javaField = $obj.attr("javaField");
													var textName = javaField.replace(name + "|", "");
													$obj.val(vv);
													$(layero).find("form").find("[name='" + javaField.replace("id|", "") + "']").val(value.name);
													break;
												case "upload":
													break;
												case "input":
													$obj.val(vv);
													break;
												case "textarea":
													$obj.val(vv);
													break;
												case "datePicker":
													$obj.val(vv);
													break;
												case "treeSelect":
													var name = $obj.attr("name");
													$obj.val(vv);
													$(layero).find("form").find("[name='" + name.replace(".id", ".name") + "']").val(value.name);
													break;
												case "iconSelect":
													$obj.val(vv);
													break;
												case "summernote":
													$obj.summernote("code",vv);
												default:
													break
											}
										})
									} else {
										var $obj = $(layero).find("form").find("[name='" + k + "']");
										var componentType = $obj.attr("component-type");
										switch(componentType) {
											case "select":
												$obj.val(value).trigger("change");
												break;
											case "checkbox":
												var splitData = value.split(",");
												$.each(splitData, function(i, v) {
													$(layero).find("form").find("[name='" + k + "List'][value='" + v + "']").iCheck('check')
												});
												break;
											case "radio":
												$(layero).find("form").find("[name='" + k + "'][value='" + value + "']").iCheck('check');
												break;
											case "grid":
												break;
											case "upload":
												$(layero).find("form").find("input[name='" + k + "']").val(value);
												var componentAccept = $obj.attr("component-accept");
												if(value) {
													LayerUtil.ajax({
														url: layui.admin.basePath + '/system/sysFile/getFileList?groupId=' + value,
														async: false,
														type: 'post',
														success: function(result) {
															var data = result.data.fileListMap.files;
															var html = "";
															if(data) {
																for(var i = 0; i < data.length; i += 1) {
																	html += "<div>";
																	html += data[i].name;
																	html += "&emsp;";
																	if(componentAccept=="pic"){
																		html += "<img src='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' style='max-width:150px;'>"
																	}
																	if(layui.admin.lang()) {
																		html += "<a class='webUploadUpload' data-fileId='" + data[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' download='" + data[i].name + "' target='_blank'>Download</a>";
																		html += "&nbsp;";
																		html += "<a class='webUploadRemove' data-fileId='" + data[i].id + "'>Delete</a>"
																	} else {
																		html += "<a class='webUploadUpload' data-fileId='" + data[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' download='" + data[i].name + "' target='_blank'>下载</a>";
																		html += "&nbsp;";
																		html += "<a class='webUploadRemove' data-fileId='" + data[i].id + "'>删除</a>"
																	}
																	html += "</div>";
																}
															}
															$(layero).find("form").find("div[name='" + k + "']").find(".fileListDiv").html(html)
														}
													})
												}
												break;
											case "uploadsec":
												$(layero).find("form").find("input[name='" + k + "']").val(value);
												var componentAccept = $obj.attr("component-accept");
												if(value) {
													LayerUtil.ajax({
														url: layui.admin.basePath + '/system/sysFile/getFileList?groupId=' + value,
														async: false,
														type: 'post',
														success: function(result) {
															var dataList = result.data.fileListMap.files;
															var html = "";
															if(dataList) {
																for(var i = 0; i < dataList.length; i++) {
																	html += "<div style='margin-top:10px;'>";
																	html += '<select name="secLevel-single-file" class="form-control-sec">';
																	html +=  myHelper.getSecOptionHtml(data.secLevel,dataList[i].secretLevel);
																	html +=  '</select>';
																	html += dataList[i].name;
																	html += "&emsp;";
																	if(componentAccept=="pic"){
																		html += "<img src='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + dataList[i].id + "' style='max-width:150px;'>"
																	}
																	if(layui.admin.lang()) {
																		html += "<a class='webUploadUpload' data-fileId='" + dataList[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?secret=1&fileId=" + dataList[i].id + "' download='" + dataList[i].name + "' target='_blank'>Download</a>";
																		html += "&nbsp;";
																		html += "<a class='webUploadRemove' data-fileId='" + dataList[i].id + "'>Delete</a>"
																	} else {
																		html += "<a class='webUploadUpload' data-fileId='" + dataList[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?secret=1&fileId=" + dataList[i].id + "' download='" + dataList[i].name + "' target='_blank'>下载</a>";
																		html += "&nbsp;";
																		html += "<a class='webUploadRemove' data-fileId='" + dataList[i].id + "'>删除</a>"
																	}
																	html += "</div>";
																}
															}
															$(layero).find("form").find("div[name='" + k + "']").find(".fileListDiv").html(html)
														}
													})
												}
												break;
											case "input":
												$obj.val(value);
												break;
											case "textarea":
												$obj.val(value);
												break;
											case "datePicker":
												$obj.val(value);
												break;
											case "treeSelect":
												break;
											case "iconSelect":
												$obj.val(value);
												break;
											case "summernote":
												$obj.summernote("code",value);
											default:
												break
										}
									}
								});
								if(options.setDataWidthAjax) {
									if(options.initData) {
										$.each(options.initData, function(k, v) {
											if($.isPlainObject(v)) {
												$.each(v, function(vk, vv) {
													$(layero).find("[name='" + k + "." + vk + "']").val(vv)
												})
											} else {
												$(layero).find("[name='" + k + "']").val(v)
											}
										})
									}
									if(typeof window.formFuction == "function") {
										window.formFuction(res, layero, options.formFunctionData);
										delete window.formFuction
									}
								}
							}
						})
					}
					if(options.full) {
						layui.layer.full(index)
					}
					if(options.setDataWithoutAjax) {
						if(options.initData) {
							$.each(options.initData, function(k, v) {
								if($.isPlainObject(v)) {
									$.each(v, function(vk, vv) {
										$(layero).find("[name='" + k + "." + vk + "']").val(vv)
									})
								} else {
									$(layero).find("[name='" + k + "']").val(v)
								}
							})
						}
						if(typeof window.formFuction == "function") {
							window.formFuction("", layero, options.formFunctionData);
							delete window.formFuction
						}
					}
				})
			},
			cancel: function(index) {},
			end: options.end
		};
		if(opt.btn.length == 2) {
			opt.yes = function(index, layero) {
				func1.call(this, index, layero, arguments)
			}
		} else if(opt.btn.length == 3) {
			opt.btn1 = function(index, layero) {
				func1.call(this, index, layero, arguments)
			};
			opt.btn2 = function(index, layero) {
				func2.call(this, index, layero, arguments);
				return false
			}
		} else if(opt.btn.length == 4) {
			opt.btn1 = function(index, layero) {
				func1.call(this, index, layero, arguments)
			};
			opt.btn2 = function(index, layero) {
				func2.call(this, index, layero, arguments);
				return false
			};
			opt.btn3 = function(index, layero) {
				func3.call(this, index, layero, arguments);
				return false
			}
		}
		return layui.layer.open($.extend({
			type: 1,
			title: layui.admin.lang() ? 'Tips' : '提示',
			content: '',
			id: 'LAY-system-view-popup',
			skin: 'layui-layer-admin' + (skin ? ' ' + skin : ''),
			shadeClose: true,
			closeBtn: false,
			success: function(layero, index) {
				var elemClose = $('<i class="layui-icon" close>&#x1006;</i>');
				layero.append(elemClose);
				elemClose.on('click', function() {
					layer.close(index)
				});
				typeof success === 'function' && success.apply(this, arguments)
			},
			end: function() {
				typeof end === 'function' && end.apply(this, arguments)
			},
			maxmin: typeof options.maxmin != "undefined" ? options.maxmin : options.full ? 0 : 1,
			closeBtn: 1
		}, opt))
	},
	openDialogView: function(options) {
		var _this = this;
		var success = options.success,
			skin = options.skin;
		delete options.success;
		delete options.skin;
		var func1 = options.func1;
		var func2 = options.func2;
		var func3 = options.func3;
		var opt = {
			title: options.title,
			formLay: options.formLay,
			initData: options.initData,
			url: options.url,
			area: [options.width || '900px', options.height || '500px'],
			offset: $(".wy-layer").length > 0 ? [$(".wy-layer").last().css("top").replace("px", "") * 1 + 10 + "px", $(".wy-layer").last().css("left").replace("px", "") * 1 + 10 + "px"] : 'auto',
			id: options.id ? options.id : 'LAY-popup-' + options.formLay,
			shadeClose: false,
			skin: options.full ? '' : 'wy-layer',
			success: function(layero, index) {
				layui.view(this.id).render(options.url, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null).done(function() {
					if(options.formLay) {
						_this.setInputFormLang(options.formLay, layero)
					}
					typeof options.formInit === 'function' && options.formInit.call(this, typeof options.initData === 'function' ? options.initData.call(this) : options.initData || null);
					if(options.actionUrl && options.initData) {
						LayerUtil.ajax({
							url: options.actionUrl,
							data: options.actionType == "post" ? $.isPlainObject(options.initData) ? JSON.stringify(options.initData) : "" : "",
							type: options.actionType ? options.actionType : 'get',
							success: function(res) {
								var actionDataName = options.actionDataName ? options.actionDataName : "";
								var data = res.data;
								if(res.data && res.data[actionDataName]) {
									data = res.data[actionDataName]
								}
								$.each(data, function(k, value) {
									if($.isPlainObject(value)) {
										$.each(value, function(vk, vv) {
											var $obj = $(layero).find("form").find("[name='" + k + "." + vk + "']");
											if($obj.attr("type") != "hidden") {
												var componentType = $obj.attr("component-type");
												switch(componentType) {
													case "select":
														$obj.select2("destroy");
														var componentId = $obj.attr("component-id");
														if(componentId) {
															layui.admin.getDictionary('data-params', componentId, function(res) {
																var arr = [];
																$.each(res, function(index, term) {
																	if(term.member == vv) {
																		arr.push(term["memberName" + layui.admin.lang()])
																	}
																});
																var html = "<div class='form-control'>" + arr.join("，") + "</div>";
																$obj.replaceWith(html)
															})
														} else {
															$obj.replaceWith('<div class="form-control"></div>')
														}
														break;
													case "checkbox":
														var componentId = $(layero).find("form").find("div[name='" + k + "." + vk + "']").attr("component-id");
														if(componentId) {
															layui.admin.getDictionary('data-params', componentId, function(res) {
																var splitData = vv.split(",");
																var arr = [];
																$.each(splitData, function(i, v) {
																	$.each(res, function(index, term) {
																		if(term.member == v) {
																			arr.push(term["memberName" + layui.admin.lang()])
																		}
																	})
																});
																var html = "<div class='form-control'>" + arr.join("，") + "</div>";
																$(layero).find("form").find("div[name='" + k + "." + vk + "List']").replaceWith(html)
															})
														} else {
															$(layero).find("form").find("div[name='" + k + "." + vk + "List']").replaceWith('<div class="form-control"></div>')
														}
														break;
													case "radio":
														var componentId = $(layero).find("form").find("div[name='" + k + "." + vk + "']").attr("component-id");
														if(componentId) {
															layui.admin.getDictionary('data-params', componentId, function(res) {
																var arr = [];
																$.each(res, function(index, term) {
																	if(term.member == vv) {
																		arr.push(term["memberName" + layui.admin.lang()])
																	}
																});
																var html = "<div class='form-control'>" + arr.join("，") + "</div>";
																$(layero).find("form").find("div[name='" + k + "." + vk + "']").replaceWith(html)
															})
														} else {
															$(layero).find("form").find("div[name='" + k + "." + vk + "']").replaceWith('<div class="form-control"></div>')
														}
														break;
													case "grid":
														$obj.siblings(".tag-div-button").remove();
														$obj.replaceWith("<div class='form-control'>" + value.name + "</div>");
														break;
													case "upload":
														break;
													case "input":
														$obj.siblings(".tag-div-button").remove();
														$obj.replaceWith("<div class='form-control'>" + vv + "</div>");
														break;
													case "textarea":
														$obj.siblings(".tag-div-button").remove();
														vv = vv.replace(/(\r\n|\n|\r)/gm,"<br>");
														$obj.replaceWith("<div class='form-control'>" + vv + "</div>");
														break;
													case "datePicker":
														$obj.siblings(".tag-div-button").remove();
														$obj.replaceWith("<div class='form-control'>" + vv + "</div>");
														break;
													case "treeSelect":
														if(!value.name) {
															value.name = ""
														}
														$obj.siblings(".tag-div-button").remove();
														$obj.replaceWith("<div class='form-control'>" + value.name + "</div>");
														break;
													case "iconSelect":
														$obj.siblings(".tag-div-button").remove();
														$obj.replaceWith("<div class='form-control'>" + vv + "</div>");
														break;
													case "summernote":
														$obj.summernote("destroy");
														$obj.siblings(".tag-div-button").remove();
														$obj.replaceWith("<div class='form-control'>" + vv + "</div>");
													default:
														break
												}
											}
										})
									} else {
										var $obj = $(layero).find("form").find("[name='" + k + "']");
										if($obj.attr("type") != "hidden") {
											var componentType = $obj.attr("component-type");
											switch(componentType) {
												case "select":
													$obj.select2("destroy");
													var componentId = $obj.attr("component-id");
													if(componentId) {
														layui.admin.getDictionary('data-params', componentId, function(res) {
															var arr = [];
															$.each(res, function(index, term) {
																if(term.member == value) {
																	arr.push(term["memberName" + layui.admin.lang()])
																}
															});
															var html = "<div class='form-control'>" + arr.join("，") + "</div>";
															$obj.replaceWith(html)
														})
													} else {
														$obj.replaceWith('<div class="form-control"></div>')
													}
													break;
												case "checkbox":
													var componentId = $(layero).find("form").find("div[name='" + k + "']").attr("component-id");
													if(componentId) {
														layui.admin.getDictionary('data-params', componentId, function(res) {
															var splitData = value.split(",");
															var arr = [];
															$.each(splitData, function(i, v) {
																$.each(res, function(index, term) {
																	if(term.member == v) {
																		arr.push(term["memberName" + layui.admin.lang()])
																	}
																})
															});
															var html = "<div class='form-control'>" + arr.join("，") + "</div>";
															$(layero).find("form").find("div[name='" + k + "']").replaceWith(html)
														})
													} else {
														$(layero).find("form").find("div[name='" + k + "']").replaceWith('<div class="form-control"></div>')
													}
													break;
												case "radio":
													var componentId = $(layero).find("form").find("div[name='" + k + "']").attr("component-id");
													if(componentId) {
														layui.admin.getDictionary('data-params', componentId, function(res) {
															var arr = [];
															$.each(res, function(index, term) {
																if(term.member == value) {
																	arr.push(term["memberName" + layui.admin.lang()])
																}
															});
															var html = "<div class='form-control'>" + arr.join("，") + "</div>";
															$(layero).find("form").find("div[name='" + k + "']").replaceWith(html)
														})
													} else {
														$(layero).find("form").find("div[name='" + k + "']").replaceWith('<div class="form-control"></div>')
													}
													break;
												case "grid":
													break;
												case "upload":
													$obj.find(".btn-white").hide();
													$(layero).find("form").find("input[name='" + k + "']").val(value);
													var componentAccept = $obj.attr("component-accept");
													if(value) {
														LayerUtil.ajax({
															url: layui.admin.basePath + '/system/sysFile/getFileList?groupId=' + value,
															async: false,
															type: 'post',
															success: function(result) {
																var data = result.data.fileListMap.files;
																var html = "";
																if(data) {
																	for(var i = 0; i < data.length; i += 1) {
																		html += "<div>";
																		html += data[i].name;
																		html += "&emsp;";
																		if(componentAccept=="pic"){
																			html += "<img src='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' style='max-width:150px;'>"
																		}
																		if(layui.admin.lang()) {
																			html += "<a class='webUploadUpload' data-fileId='" + data[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' download='" + data[i].name + "' target='_blank'>Download</a>"
																		} else {
																			html += "<a class='webUploadUpload' data-fileId='" + data[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' download='" + data[i].name + "' target='_blank'>下载</a>"
																		}
																		html += "</div>";
																	}
																}
																$obj.replaceWith("<div class='form-control'>" + html + "</div>")
															}
														})
													}
													break;
												case "uploadsec":
													$obj.find(".btn-white").hide();
													$(layero).find("form").find("input[name='" + k + "']").val(value);
													var componentAccept = $obj.attr("component-accept");
													if(value) {
														LayerUtil.ajax({
															url: layui.admin.basePath + '/system/sysFile/getFileList?groupId=' + value,
															async: false,
															type: 'post',
															success: function(result) {
																var data = result.data.fileListMap.files;
																var html = "";
																if(data) {
																	for(var i = 0; i < data.length; i += 1) {
																		html += "<div>";
																		var secValue = "";
																		layui.admin.getDictionary('data-params', 'sys_sec_level', function(res) {
																			$.each(res, function(index, term) {
																				if(term.member == data[i].secretLevel) {
																					secValue = term["memberName" + layui.admin.lang()];
																				}
																			})
																		})
																		if(!secValue){
																			secValue = "公开";
																		}
																		html += "<span style='margin-right:20px;line-height:25px;display:inline-block;width:60px;'>"+secValue+"</span>";
																		html += data[i].name;
																		html += "&emsp;";
																		if(componentAccept=="pic"){
																			html += "<img src='" + layui.admin.basePath + "/system/sysFile/fileDownload?fileId=" + data[i].id + "' style='max-width:150px;'>"
																		}
																		if(layui.admin.lang()) {
																			html += "<a class='webUploadUpload' data-fileId='" + data[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?secret=1&fileId=" + data[i].id + "' download='" + data[i].name + "' target='_blank'>Download</a>"
																		} else {
																			html += "<a class='webUploadUpload' data-fileId='" + data[i].id + "' href='" + layui.admin.basePath + "/system/sysFile/fileDownload?secret=1&fileId=" + data[i].id + "' download='" + data[i].name + "' target='_blank'>下载</a>"
																		}
																		html += "</div>";
																	}
																}
																$obj.replaceWith("<div class='form-control'>" + html + "</div>")
															}
														})
													}
													break;
												case "input":
													$obj.siblings(".tag-div-button").remove();
													$obj.replaceWith("<div class='form-control'>" + value + "</div>");
													break;
												case "textarea":
													$obj.siblings(".tag-div-button").remove();
													value = value.replace(/(\r\n|\n|\r)/gm,"<br>");
													$obj.replaceWith("<div class='form-control'>" + value + "</div>");
													break;
												case "datePicker":
													$obj.siblings(".tag-div-button").remove();
													$obj.replaceWith("<div class='form-control'>" + value + "</div>");
													break;
												case "treeSelect":
													break;
												case "iconSelect":
													$obj.siblings(".tag-div-button").remove();
													$obj.replaceWith("<div class='form-control'>" + value + "</div>");
													break;
												case "summernote":
													$obj.summernote("destroy");
													$obj.siblings(".tag-div-button").remove();
													$obj.replaceWith("<div class='form-control'>" + value + "</div>");
												default:
													break
											}
										}
									}
								});
								layui.admin.getDictionary('page-form-field', options.formLay, function(d) {
									$.each(d, function(idx, item) {
										var $obj = $(layero).find('[name="' + item.member + '"]');
										if($obj.length > 0) {
											$obj.closest("fieldset.set").html("<div class='form-control'></div>")
										}
									})
								});
								if(options.setDataWidthAjax) {
									if(options.initData) {
										$.each(options.initData, function(k, v) {
											if($.isPlainObject(v)) {
												$.each(v, function(vk, vv) {
													$(layero).find("[name='" + k + "." + vk + "']").val(vv)
												})
											} else {
												$(layero).find("[name='" + k + "']").val(v)
											}
										})
									}
									if(typeof window.formFuction == "function") {
										window.formFuction(res, layero, options.formFunctionData);
										delete window.formFuction
									}
								}
								if(data.procInsId && $(layero).find("#procImgDiv").length > 0) {
									ProcUtil.drawProcPic($(layero).find("#procImgDiv"), data)
								}
								if(data.procInsId && $(layero).find("#procLogDiv").length > 0) {
									ProcUtil.drawProcLog($(layero).find("#procLogDiv"), data)
								}
								if($(layero).find("#requeridFormTab").length > 0) {
									$(layero).find("#requeridFormTab").text(layui.admin.lang() ? "Form" : "文单")
								}
								if($(layero).find("#procPicTab").length > 0) {
									$(layero).find("#procPicTab").text(layui.admin.lang() ? "Process chart" : "流程图")
								}
								if($(layero).find("#procLogTab").length > 0) {
									$(layero).find("#procLogTab").text(layui.admin.lang() ? "Process log" : "流程日志")
								}
								$(layero).on("click", "#showProcImgDiv", function() {
									$(layero).find("#procImgDiv").toggle();
									$(layero).find("#procLogDiv").hide()
								});
								$(layero).on("click", "#showProcLogDiv", function() {
									$(layero).find("#procLogDiv").toggle();
									$(layero).find("#procImgDiv").hide()
								})
							}
						})
					}
					if(options.full) {
						layui.layer.full(index)
					}
					if(options.setDataWithoutAjax) {
						if(options.initData) {
							$.each(options.initData, function(k, v) {
								if($.isPlainObject(v)) {
									$.each(v, function(vk, vv) {
										$(layero).find("[name='" + k + "." + vk + "']").val(vv)
									})
								} else {
									$(layero).find("[name='" + k + "']").val(v)
								}
							})
						}
						if(typeof window.formFuction == "function") {
							window.formFuction("", layero, options.formFunctionData);
							delete window.formFuction
						}
					}
				})
			},
			cancel: function(index) {},
			end: options.end
		};
		return layui.layer.open($.extend({
			type: 1,
			title: layui.admin.lang() ? 'Tips' : '提示',
			content: '',
			id: 'LAY-system-view-popup',
			skin: 'layui-layer-admin' + (skin ? ' ' + skin : ''),
			shadeClose: true,
			closeBtn: false,
			btn: layui.admin.lang() ? ['Close'] : ['关闭'],
			success: function(layero, index) {
				var elemClose = $('<i class="layui-icon" close>&#x1006;</i>');
				layero.append(elemClose);
				elemClose.on('click', function() {
					layer.close(index)
				});
				typeof success === 'function' && success.apply(this, arguments)
			},
			end: function() {
				typeof end === 'function' && end.apply(this, arguments)
			},
			maxmin: typeof options.maxmin != "undefined" ? options.maxmin : options.full ? 0 : 1,
			closeBtn: 1
		}, opt))
	},
	info: function(msg) {
		return layui.layer.msg(msg)
	},
	warning: function(msg) {
		return layui.layer.msg(msg, {
			icon: 0
		})
	},
	success: function(msg) {
		return layui.layer.msg(msg, {
			icon: 1
		})
	},
	error: function(msg) {
		return layui.layer.msg(msg, {
			icon: 2
		})
	},
	alert: function(msg) {
		layui.layer.alert(msg, {
			skin: 'layui-layer-lan',
			area: ['auto', 'auto'],
			icon: 0,
			closeBtn: 0,
			anim: 4
		})
	},
	loading: function(msg) {
		if(!msg) {
			msg = '处理中，请稍等...'
		}
		var index = layui.layer.msg(msg, {
			icon: 16,
			shade: 0.01,
			time: 999999999
		});
		return index
	},
	close: function(index) {
		if(index) {
			layui.layer.close(index)
		} else {
			layui.layer.closeAll()
		}
	},
	confirm: function(msg, succFuc, cancelFuc) {
		layui.layer.confirm(msg, {
			icon: 3,
			title: layui.admin.lang() ? 'Tips' : '系统提示',
			btn: layui.admin.lang() ? ['yes', 'no'] : ['是', '否']
		}, function(index) {
			if(typeof succFuc == 'function') {
				succFuc()
			} else {
				location = succFuc
			}
			layui.layer.close(index)
		}, function(index) {
			if(cancelFuc) {
				cancelFuc()
			}
			layui.layer.close(index)
		});
		return false
	},
	getMsgLang: function() {
		return layui.admin.lang() ? 'msg_en' : 'msg'
	},
	ajax: function(options) {
		var success = options.success;
		delete options.success;
		var loadingIndex;
		if(options.shade) {
			loadingIndex = LayerUtil.loading()
		}
		var opt = {
			type: 'get',
			tipOk: false,
			loading: false,
			contentType: "application/json"
		};
		opt = $.extend(true, opt, options);
		opt.success = function(res) {
			if(res.code == 0 && typeof success == "function") {
				success(res)
			}
			if(options.shade) {
				LayerUtil.close(loadingIndex)
			}
		};
		layui.admin.req(opt)
	},
	getBtnsStrFromMenuData: function(hashUrl, procBtns, removeBtns) {
		var hashHref = "";
		var menuData = layui.admin.getSessionList("page");
		if(hashUrl) {
			hashHref = hashUrl
		} else {
			hashHref = "/" + layui.router().path.join('/')
		}
		var pMenu = $.grep(menuData, function(obj, index) {
			return obj.pageUrl == hashHref
		});
		var tableBtns, toolBtns;
		if(pMenu.length > 0) {
			toolBtns = $.grep(menuData, function(obj, index) {
				return obj.parentID == pMenu[0].pageID && obj.type == 3
			});
			tableBtns = $.grep(menuData, function(obj, index) {
				return obj.parentID == pMenu[0].pageID && obj.type == 4
			})
		}
		if(tableBtns && procBtns) {
			tableBtns = procBtns.concat(tableBtns)
		} else if(procBtns) {
			tableBtns = procBtns
		}
		var toolBtnsStr = "",
			tableBtnsStr = "";
		if(toolBtns) {
			$.each(toolBtns, function(i, obj) {
				var css = "btn-success";
				if(obj.sign.indexOf("add") > -1) {
					css = "btn-success"
				} else if(obj.sign == "editBtn") {
					css = "btn-primary"
				} else if(obj.sign.indexOf("remove") > -1 || obj.sign.indexOf("del") > -1) {
					css = "btn-danger"
				} else {
					css = "btn-primary"
				}
				toolBtnsStr += '<button id="' + obj.sign + '" class="btn btn-sm ' + css + '">';
				toolBtnsStr += '<i class="fa ' + obj.pageIcon + '"></i> ' + obj["pageName" + layui.admin.lang()];
				toolBtnsStr += '</button>'
			})
		}
		if(tableBtns) {
			tableBtnsStr += '<div class="show-btn-group"><i class="fa fa-ellipsis-v" aria-hidden="true"></i><div class="hide-btn-group">';
			$.each(tableBtns, function(i, obj) {
				if(!removeBtns){
					tableBtnsStr += '<a class="btn ' + obj.sign + '" dealname="' + obj["pageName" + layui.admin.lang()] + '">' + obj["pageName" + layui.admin.lang()] + '</a>'
				}
				else if(removeBtns.indexOf(obj.sign) == -1){
					tableBtnsStr += '<a class="btn ' + obj.sign + '" dealname="' + obj["pageName" + layui.admin.lang()] + '">' + obj["pageName" + layui.admin.lang()] + '</a>'
				}
			});
			tableBtnsStr += '</div></div>'
		}
		return {
			tableBtnsStr: tableBtnsStr,
			toolBtnsStr: toolBtnsStr
		}
	},
	openZtree: function(_obj, _title) {
		var isParent = _obj.isParent;
		var actionUrl = _obj.allData.url;
		var check = true;
		var chooseParent = isParent;
		layui.layer.open({
			type: 1,
			id: 'LAY-popup-ztreeTag',
			url: 'admin/tag/ztreeTag',
			title: _title,
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
						setting.check = {
							enable: true,
							chkStyle: "checkbox",
							chkboxType: {
								"Y": "ps",
								"N": "ps"
							}
						};
						LayerUtil.ajax({
							url: layui.admin.basePath + _obj.detail.url,
							type: _obj.detail.type,
							data: {},
							success: function(result) {
								var idArr = result.data.data;
								if(_obj.allData.type == 'post') {
									if(actionUrl.indexOf('formNo=sys_roles') != -1) {} else {
										LayerUtil.ajax({
											url: layui.admin.basePath + actionUrl,
											type: _obj.allData.type,
											data: JSON.stringify({}),
											success: function(res) {
												var treeData = res.data.data;
												for(var i = 0; i < treeData.length; i += 1) {
													treeData[i].icon = ""
												}
												var zTreeObj = $.fn.zTree.init($(layero).find("#zTreeTag"), setting, treeData);
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
								} else {
									LayerUtil.ajax({
										url: layui.admin.basePath + actionUrl,
										type: _obj.allData.type,
										data: {},
										success: function(res) {
											var treeData = res.data.data;
											for(var i = 0; i < treeData.length; i += 1) {
												treeData[i].icon = ""
											}
											var zTreeObj = $.fn.zTree.init($(layero).find("#zTreeTag"), setting, treeData);
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
				var zTreeObj = $.fn.zTree.getZTreeObj("zTreeTag");
				if(check && !isParent) {
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
				} else if(check && isParent) {
					var nodes = zTreeObj.getCheckedNodes(true);
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
				LayerUtil.ajax({
//					url: layui.admin.basePath + _obj.save.url + '?id=' + _obj.id + '&ids=' + idStr,
					url: layui.admin.basePath + _obj.save.url,
					type: _obj.save.type,
					data: JSON.stringify({
						id: _obj.id,
						ids: idStr
					}),
					success: function(res) {
						LayerUtil.success(res[LayerUtil.getMsgLang()]);
						layui.layer.close(index)
					}
				});
				return false;
			},
			cancel: function(index) {}
		})
	}
};