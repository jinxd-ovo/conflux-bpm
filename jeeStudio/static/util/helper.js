;(function(global) {
    "use strict";

    var helper = function(options) {
    	var _this = this;
    };

    //覆写原型链，给继承者提供方法
    helper.prototype = {
    	//获得日期
    	getDate:function(timeStr,timeType){
      		var time = new Date(parseInt(timeStr));
			var y = time.getFullYear();
			var m = time.getMonth()+1;
			var d = time.getDate();
			var h = time.getHours();
			var mm = time.getMinutes();
			var s = time.getSeconds();
			if(!time || !y){
				return "";
			}else if(timeType === "year"){
				return y;
			}else if(timeType === "month"){
				return y+'-'+this.add0(m);
			}else if(timeType === "day"){
				return y+'-'+this.add0(m)+'-'+this.add0(d);
			}else{
				return y+'-'+this.add0(m)+'-'+this.add0(d)+' '+this.add0(h)+':'+this.add0(mm)+':'+this.add0(s);
			}
    	},
    	add0:function(m){
    		return m<10?'0'+m:m 
    	},
        getpath: function(w) {
        	w = w ? w : window;
        	var pathName = w.location.pathname.substring(1);
			var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'));
			if(webName == "") {
				return w.location.protocol + '//' + w.location.host;
			} else {
				return w.location.protocol + '//' + w.location.host + '/' + webName;
			}
        },
        getWebName: function(w) {
        	w = w ? w : window;
        	var pathName = w.location.pathname.substring(1);
			var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'));
			return "/"+webName;
        },
        toLowerCase: function() {
        	return str.replace(/([A-Z])/g,"-$1").toLowerCase();
        },
        toCamel: function() {
        	var re=/-(\w)/g;
		    return str.replace(re,function ($0,$1){
		        return $1.toUpperCase();
		    });
        },
        getAjaxUrl: function(urlKey){
        	var url = this.getWebName() + "/" + this.AjaxUrlConfig.baseUrl + this.AjaxUrlConfig[urlKey].url;
        	return url;
        },
        getAjaxUrlType: function(urlKey){
        	var type = this.AjaxUrlConfig[urlKey].type;
        	return type;
        },
        /**获取特定的get请求参数信息*/
		getFixUrlParams: function(name,w) {
			w = w ? w : window;
		    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
		    var search = decodeURI(w.location.search);
		    var r = search.substr(1).match(reg);
		    if (r != null) {
		    	return unescape(r[2]);
		    }
		    return null;
		},
		/**获取所有的get请求参数信息*/
		getAllUrlParam: function(w) {
			w = w ? w : window;
		   var url = decodeURI(w.location.search); //获取url中"?"符后的字串
		   var theRequest = new Object();
		   if (url.indexOf("?") != -1) {
		      var str = url.substr(1);
		      var strs = str.split("&");
		      for(var i = 0; i < strs.length; i ++) {
		         theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
		      }
		   }
		   return theRequest;
		},
		composeData: function($form) {
			var serialObj = {};
			$($form.serializeArray()).each(function() {
				var splitName = this.name.split(".");
				if(splitName.length == 2){
					if(serialObj[splitName[0]]){
						if(serialObj[splitName[0]][splitName[1]]){
							if(!(serialObj[splitName[0]][splitName[1]] instanceof Array)){
								var arr = [];
								arr.push(serialObj[splitName[0]][splitName[1]]);//放入原来的值
								serialObj[splitName[0]][splitName[1]] = arr;
							}
							serialObj[splitName[0]][splitName[1]].push(this.value);//放入新值
						}else{
							if(/^c((0[1-9]{1})|(10))List$/.test(splitName[1])){
								serialObj[splitName[0]][splitName[1]] = [this.value];
							}else{
								serialObj[splitName[0]][splitName[1]] = this.value;
							}
						}
					}else{
						serialObj[splitName[0]] = {};
						if(/^c((0[1-9]{1})|(10))List$/.test(splitName[1])){
							serialObj[splitName[0]][splitName[1]] = [this.value];
						}else{
							serialObj[splitName[0]][splitName[1]] = this.value;
						}
					}
				}else{
					if(serialObj[this.name]){
						if(!(serialObj[this.name] instanceof Array)){
							var arr = [];
							arr.push(serialObj[this.name]);//放入原来的值
							serialObj[this.name] = arr;
						}
						serialObj[this.name].push(this.value);//放入新值
					}else{
						if(/^c((0[1-9]{1})|(10))List$/.test(this.name)){
							serialObj[this.name] = [this.value];
						}else{
							serialObj[this.name] = this.value;
						}
						
					}
				}
			});
			return serialObj;
		},
		initSelect2:function(dom){
			var setWidth = "195px";
			if(dom.closest('form').attr("id") == "inputForm"){
				setWidth = "100%";
			}
			if(dom.hasClass("select2-search")){
				dom.select2({
					width: setWidth,
					language: {
						noResults: function (params) {
							return "暂无数据";
						}
					}
				});
			}	
			else{
				dom.select2({
					width: setWidth,
					minimumResultsForSearch: -1
				})
			}
		},
		initIChecks:function(dom){
			dom.find(".i-checks").iCheck({
				checkboxClass: "icheckbox_square-green",
				radioClass: "iradio_square-green"
			});
		},
		disabledIchecks:function(dom){
			dom.find(".i-checks").iCheck("disable");
		},
		getSecOptionHtml:function(value,selectedValue){
			var secOptionHtml = "";
			layui.admin.getDictionary('data-params', 'sys_sec_level', function (res) {
				var loginSecLevel = layui.admin.getSessionList("secLevel");
				if(loginSecLevel){
					loginSecLevel = parseInt(loginSecLevel);
				}
    			$.each(res, function(index, term) {
    				var selected = "";
    				if(loginSecLevel && parseInt(term.member) > loginSecLevel){
    					return;
    				}
    				if(typeof selectedValue != "undefined" && selectedValue == term.member){
    					selected = " selected"
    				}
    				if(typeof value == "undefined"){
    					secOptionHtml += "<option value='"+term.member+"' "+selected+">"+term["memberName" + layui.admin.lang()]+"</option>";
    				}
    				else{
    					if(parseInt(term.member) < parseInt(value) || parseInt(term.member) == parseInt(value)){
    						secOptionHtml += "<option value='"+term.member+"' "+selected+">"+term["memberName" + layui.admin.lang()]+"</option>";
    					}
    				}
    			});
    		});
    		return secOptionHtml;
		},
		initWebUploader:function(id,componentAccept,componentUploadpath,componentFileType){
			var _this = this;
			//附件上传下载方法
			/*$("#"+id).closest("div[component-type='upload']").on("click",".webUploadUpload",function(){
				var fileId = $(this).attr("data-fileId");
				$("#submit-form").ajaxSubmit({
					url: layui.admin.basePath + '/system/sysFile/fileDownload',
					data:{
						fileId:fileId
					},
					type:"get",
					headers: {"token" : localStorage.getItem('$tokenBPM')},
					success: function(data){
				  	}
				});
				
			});*/
			
			var template = $("#"+id).closest("div[component-type='upload']").attr("component-template") || $("#"+id).closest("div[component-type='uploadsec']").attr("component-template") || "";
			
			//附件上传删除方法
			$("#"+id).closest("div[component-type='upload']").on("click",".webUploadRemove",function(){
				var _this = this;
				var fileId = $(_this).attr("data-fileId");
				LayerUtil.ajax({
					url: layui.admin.basePath + '/system/sysFile/deleteFile?fileId=' + fileId,
					async:false,
					type: 'get',
					success: function(data){
						if(data.code == 0){
							$(_this).closest("div").remove();
							//${input}Count--;
						}else{
							LayerUtil.error(data[LayerUtil.getMsgLang()]);
						}
					}
				});
			});
			$("#"+id).closest("div[component-type='uploadsec']").on("click",".webUploadRemove",function(){
				var _this = this;
				var fileId = $(_this).attr("data-fileId");
				LayerUtil.ajax({
					url: layui.admin.basePath + '/system/sysFile/deleteFile?fileId=' + fileId,
					async:false,
					type: 'get',
					success: function(data){
						if(data.code == 0){
							$(_this).closest("div").remove();
							//${input}Count--;
						}else{
							LayerUtil.error(data[LayerUtil.getMsgLang()]);
						}
					}
				});
			});
			if(componentAccept == "pic"){
				componentAccept =  {
	                 title: 'Images',
	                 extensions: 'gif,jpg,jpeg,png,bmp,tiff,tif,GIF,JPG,JPEG,PNG,BMP,TIFF,TIF',
	                 mimeTypes: 'image/*'
	             }
			}
			else if($.isPlainObject(componentAccept)){
				componentAccept = componentAccept;
			}
			else{
				componentAccept = {};
			}
			
			var groupId = "";
			var flie_count = 0;
			var cSize = 50*1024*1024;
			
			var text1 = "文件大小不能超过";
			var text2 = "请上传";
			var text3 = "等待上传...";
			var text4 = "上传中...";
			var text5 = "下载";
			var text6 = "删除";
			var text7 = "格式文件";
			var text8 = "所选文件类型无法上传";
			var text9 = "上传成功";
			var text10 = "上传完成，但存在上传失败的文件";
			var text11 = "上传失败";
			var text12 = "未知错误";
			if(layui.admin.lang()){
				text1 = "File size cannot exceed";
				text2 = "Please upload";
				text3 = "Waiting for uploading...";
				text4 = "Uploading...";
				text5 = "Download";
				text6 = "Delete";
				text7 = "Format file";
				text8 = "The selected file type cannot be uploaded";
				text9 = "Upload success";
				text10 = "Upload completed, but there are files failed to upload";
				text11 = "Upload failure";
				text12 = "Unknown error";
			}
			
			var createObj = {
				//是否允许重复的图片
				duplicate: true,
				auto: true, // 选完文件后，是否自动上传
				swf: 'static/plugin/webuploader-0.1.5/Uploader.swf', // swf文件路径
				/* server: '${ctx}/sys/user/imageUpload',// 文件接收服务端 */
				server: layui.admin.basePath + componentUploadpath,
				pick: { 
					id: "#"+id
					//innerHTML: "添加APP图标 118*118" 
				}, // 选择文件的按钮。可选
				formData:{
					groupId: groupId, 
					chunk: flie_count
				},
				fileSingleSizeLimit: 10485760,//限制上传单个文件大小
				threads: 1,//并发数为1
				chunked: true,//分片上传-大文件的时候分片上传，默认false
	            chunkSize: cSize,
	            resize: false,
				// 只允许选择图片文件。
				accept: componentAccept,
			}
			if(componentAccept.fileNumLimit){
				createObj.fileNumLimit = componentAccept.fileNumLimit;
			}
			
			var uploader = WebUploader.create(createObj);
			uploader.onError = function( code ) {
				var max =  10485760;//
				if(code == "F_EXCEED_SIZE"){
					code = text1 + (max/1024/1024) + "M！";
				}else if(code == "Q_EXCEED_SIZE_LIMIT"){
					code = text1 + (max/1024/1024) + "M！";
				}else if(code == "Q_TYPE_DENIED"){
					if(componentAccept == "pic"){
						code = text2+"gif,jpg,jpeg,png,bmp,tiff,tif,GIF,JPG,JPEG,PNG,BMP,TIFF,TIF！"+text7;
					}else if(componentAccept.errMsg){
						code = componentAccept.errMsg;
					}else{
						code = "格式错误"
					}
				}else if(code == "Q_EXCEED_NUM_LIMIT"){
					if(layui.admin.lang()){
						code = "The quantity exceeded the limit！";
					}else{
						code = "超出上传数量限制！";
					}
					
				}
				LayerUtil.error(code);
			};
			uploader.on('uploadBeforeSend', function (obj, data, headers) {
				headers.token = localStorage.getItem('$tokenBPM');
				groupId = $("#"+id).closest(".input-group").children("input[type=hidden]").val();
				if(groupId == ""){
					groupId = WebUploader.Base.guid();
				}
				data.groupId = groupId;
				/*data.fileName = data.name;
				data.fileSize = data.size;
				data.cSize = cSize;
				data.isChunk = true;*/
				if(componentFileType=="sec"){
					data.secret = 1;
				}
			});
			uploader.on('fileQueued', function (file) {
				$("#"+id).siblings(".fileListDiv").append('<div style="color: #676A6C;" id="'+file.id+'" class="item">' +
						'<span class="text">'+text3+'</span> ' +file.name +
						'</div>');
			});
			uploader.on( 'uploadProgress', function( file, percentage ) {
				$("#"+id).siblings(".fileListDiv").find("#" + file.id).find('.text').text(text4+(percentage * 100).toFixed(2) + '%...');
				var $percent = $("#"+id).siblings(".fileListDiv").find("#" + file.id).find('.progress span');
				// 避免重复创建
				if ( !$percent.length ) {
					$percent = $('<p class="progress"><span></span></p>')
							.appendTo($("#"+id).siblings(".fileListDiv").find("#" + file.id))
							.find('span');
				}
				$percent.css( 'width', percentage * 100 + '%' );
			});
			uploader.on( 'uploadSuccess', function( file ,result) {
				if(result.token){
                	localStorage.setItem("$tokenBPM",result.token);
                }
				
				var ajaxUrl = layui.admin.basePath + componentUploadpath +"?groupId="+groupId+"&fileName="+file.name+"&fileSize="+file.size+"&chunk="+Math.ceil(file.size / (cSize))+"&isChunk=false&isEncoding=false&cSize="+cSize;
				if(componentFileType=="sec"){
					ajaxUrl += "&secret=1";
				}
				if(template){
					ajaxUrl += "&template="+template;
				}
				LayerUtil.ajax({
					url: ajaxUrl,
					/*data: JSON.stringify({
						"groupId":$("#"+id).siblings("input[type=hidden]").val(),
						"fileName":file.name,
						"fileSize":file.size,
						"chunk":Math.ceil(file.size / (cSize)),
						"isChunk":"false",
						"cSize":cSize,
						"isEncoding":false
					}),*/
					type: 'post',
					success: function(result){
	        			$("#"+id).siblings("input[type=hidden]").val(result.data.map.groupId);
						//这里手动修改webuploader的参数
						uploader.options.formData.groupId = result.data.map.groupId;
							
						$("#"+id).siblings(".fileListDiv").find("#" + file.id).remove();
						
						$.each(result.data.map.successList,function(index, data){
							var html="<div>";
							//html+="<span id='"+data.id+"'name='${input}Allow'>";
							//html+="<span><a href='javascript:;' i='${ctx}${uploadPath}/fileDownloadThumbNew?fileId=" + data.id + "' class='Slide ${input}Slide'><img id='img" + data.id + "' align='middle' style='margin-left:1%;margin-bottom:0px;margin-top:1%'	  src='${ctx}${uploadPath}/fileDownloadThumbNew?fileId=" + data.id + "' width='${width == null ? '100' : width}px' height='${height == null ? '60' : height}px'  data="+data.id+"   /></a></span>";
							if(componentFileType=="sec"){
								var secValue = $("#"+id).closest("form").find("select[name='secLevel']").val() || undefined;
								var secHtml = '<select name="secLevel-single-file" class="form-control-sec">';
								secHtml += _this.getSecOptionHtml(secValue);
			            		secHtml += '</select>';
								html = "<div style='margin-top:10px;'>"+secHtml;
							}
							html+=data.name;
							html+="&emsp;";
							if(componentFileType=="sec"){
								html+="<a class='webUploadUpload' data-fileId='" + data.id + "' href='"+layui.admin.basePath+"/system/sysFile/fileDownload?secret=1&fileId="+data.id+"' download='"+data.name+"' target='_blank'>"+text5+"</a>";
							}else{
								html+="<a class='webUploadUpload' data-fileId='" + data.id + "' href='"+layui.admin.basePath+"/system/sysFile/fileDownload?fileId="+data.id+"' download='"+data.name+"' target='_blank'>"+text5+"</a>";
							}
							html+="&nbsp;";
							html+="<a class='webUploadRemove' data-fileId='" + data.id + "'>"+text6+"</a>";
							html+="</div>";
							$("#"+id).siblings(".fileListDiv").append(html);
							//${input}Count++;
						});
						//$(".${input}Slide").simpleSlide();
						$.each(result.data.map.failList,function(index,data){
							$("#"+id).siblings(".fileListDiv").html(
									$("#"+id).siblings(".fileListDiv").html()
									+ "<span>"
									+ "<span style='color:red;'>" + data.name + "&emsp;"+text8+"</span>"
									+ "<br/>"
									+ "</span>"
							);
						});
		
						if(result.data.map.successList.length > 0 && result.data.map.failList.length == 0) {
							LayerUtil.success(text9);
						} else if(result.data.map.successList.length > 0 && result.data.map.failList.length > 0) {
							LayerUtil.warning(text10);
						} else if(result.data.map.successList.length == 0 && result.data.map.failList.length > 0) {
							LayerUtil.error(text11);
						} else {
							LayerUtil.error(text12);
						}
	        		}
				});
			});
			uploader.on( 'uploadComplete', function( file ) {  
				flie_count = 0; 
			});
		},
		initSummernote: function(dom) {
			dom.summernote({
		        height: 200,
		        tabsize: 2,
		        lang: 'zh-CN',
		        disableDragAndDrop:true,
		        callbacks:{
		        	onChange:function(contents,$editable){
		        		dom.text(contents);
		        	}
		        },
		        maximumImageFileSize:307200
		    });
		},
		editTableRow: function(dictionaryId, columnObj ,$element, oldValue, oldKey) {
//			$element.attr('editStatus', true);
			layui.admin.getDictionary('page-form-field', dictionaryId, function(d) {
				$.each(d, function(idx, item) {
					//var $obj = formObj.find('[name="' + item.member + '"]');
					var $obj = $element;
					var componentType = columnObj.componentType;
					var required = columnObj.componentRequired == true ? "required" : "";
					var disabled = columnObj.componentDisabled;
					var fieldName = columnObj.fieldName;
					var componentName = columnObj.componentName;//控件别名 如user01.id
					if(item.member == fieldName || item.member == componentName ){
						switch(componentType) {
							case "input":
								if(disabled != true && disabled != "true"){
									$obj.html('<fieldset class="set"><input name="'+fieldName+'" component-type="input" type="text" class="form-control ' + required + '" value="'+oldValue+'"/></fieldset>')
								}
								break;
							case "textarea":
								if(disabled != true && disabled != "true"){
									$obj.html('<fieldset class="set"><textarea name="'+fieldName+'" component-type="textarea" rows="1" class="form-control ' + required + '">'+oldValue+'</textarea></fieldset>');
								}
								break;
							case "select":
								if(disabled != true && disabled != "true"){
									var componentId = columnObj.componentId;
									if(componentId) {
										layui.admin.getDictionary('data-params', componentId, function(res) {
											var html = '<fieldset class="set"><select name="'+fieldName+'" component-type="select" component-id="yes_no" class="form-control ' + required + '">';
											$.each(res, function(index, term) {
												if(term.member == oldValue){
													html += "<option value='" + term.member + "' selected>" + term["memberName" + layui.admin.lang()] + "</option>"
												}else{
													html += "<option value='" + term.member + "'>" + term["memberName" + layui.admin.lang()] + "</option>"
												}
											});
											html += '</select></fieldset>';
											$obj.html(html);
											myHelper.initSelect2($obj.find("select"))
										})
									} 
								}
								break;
							case "checkbox":
								if(disabled != true && disabled != "true"){
									oldValue = oldValue.split(",");
									var componentId = columnObj.componentId;
									if(componentId) {
										layui.admin.getDictionary('data-params', componentId, function(res) {
											var html = '<fieldset class="set"><div name="'+fieldName+'" component-type="checkbox" component-id="yes_no" class="i-checks-div ">';
											$.each(res, function(index, term) {
												if(oldValue.indexOf(term.member) > -1){
													html += "<input type='checkbox' name='" + item.member + "List' value='" + term.member + "' class='i-checks " + required + "' checked='checked'>" + term["memberName" + layui.admin.lang()]
												}else{
													html += "<input type='checkbox' name='" + item.member + "List' value='" + term.member + "' class='i-checks " + required + "'>" + term["memberName" + layui.admin.lang()]
												}
											});
											html += '</div></fieldset>';
											$obj.html(html);
											myHelper.initIChecks($obj)
										})
									} else {
										myHelper.initIChecks($obj)
									}
								}
								break;
							case "radio":
								if(disabled != true && disabled != "true"){
									var componentId = columnObj.componentId;
									if(componentId) {
										layui.admin.getDictionary('data-params', componentId, function(res) {
											var html = '<fieldset class="set"><div name="'+fieldName+'" component-type="radio" component-id="yes_no" class="i-checks-div ">';
											$.each(res, function(index, term) {
												if(required && index == 0 && !oldValue) {
													html += "<input type='radio' name='" + item.member + "' value='" + term.member + "' class='i-checks' checked='checked'>" + term["memberName" + layui.admin.lang()]
												} else {
													if(term.member == oldValue){
														html += "<input type='radio' name='" + item.member + "' value='" + term.member + "' class='i-checks' checked='checked'>" + term["memberName" + layui.admin.lang()]
													}else{
														html += "<input type='radio' name='" + item.member + "' value='" + term.member + "' class='i-checks'>" + term["memberName" + layui.admin.lang()]
													}
												}
											});
											html += '</div></fieldset>';
											$obj.html(html);
											myHelper.initIChecks($obj)
										})
									} else {
										myHelper.initIChecks($obj)
									}
								}
								break;
							case "datePicker":
								if(disabled != true && disabled != "true"){
									$obj.html('<fieldset class="set"><input name="'+fieldName+'" component-type="datePicker" readonly="readonly" onclick="WdatePicker({dateFmt:\''+columnObj.componentDateFmt+'\'})" type="text" class="Wdate form-control ' + required + '" value="'+oldValue+'"/></fieldset>')
								}
								break;
							case "treeSelect":
								if(disabled != true && disabled != "true"){
									var textName = item.member.replace(".id", ".name");
									var componentActionUrl = columnObj.componentActionUrl;
									var componentSync = columnObj.componentSync;
									var componentCheck = columnObj.componentCheck;
									var componentChooseParent = columnObj.componentChooseParent;
									var html = '<div class="adapt" style="margin:0px"><fieldset class="set">'
										+'<div name="'+componentName+'" component-type="treeSelect" actionUrl="'+componentActionUrl+'" sync="'+componentSync+'" check="'+componentCheck+'" chooseParent="'+componentChooseParent+'" class="tag-div ">'
											+'<input name="' + item.member + '" value="'+oldKey+'" type="hidden">'
											+'<input name="' + textName + '" readonly="readonly" class="form-control ' + required + '" type="text" value="'+oldValue+'"><a class="tag-div-button"><i class="fa fa-search"></i></a>'
										+'</div>'
									+'</fieldset></div>';
									$obj.html(html);
								}
								break;
							/*case "iconSelect":
								var html = '<input name="' + item.member + '" readonly="readonly" class="form-control ' + required + '" type="text" value=""><a class="tag-div-button"><i class="fa fa-search"></i></a>';
								$obj.append(html);
								break;*/
							case "grid":
								if(disabled != true && disabled != "true"){
									var tableName = columnObj.tableName;
									var searchKey = columnObj.searchKey;
									var fieldKeys = columnObj.fieldKeys;
									var fieldLabels = columnObj.fieldLabels;
									var searchLabel = columnObj.searchLabel;
									var javaField = columnObj.javaField;
									var textName = item.member.replace(".id", "") + "." + javaField.replace(item.member + "|", "");
									var html = '<div class="adapt" style="margin:0px"><fieldset class="set">'
										+'<div name="'+componentName+'" component-type="grid" class="tag-div " tableName="'+tableName+'" searchKey="'+searchKey+'" fieldKeys="'+fieldKeys+'" fieldLabels="'+fieldLabels+'" searchLabel="'+searchLabel+'" javaField="'+javaField+'">'
									 		+'<input name="' + item.member + '" value="'+oldKey+'" type="hidden">'
									 		+'<input name="' + textName + '" readonly="readonly" class="form-control ' + required + '" type="text" value="'+oldValue+'"><a class="tag-div-button"><i class="fa fa-search"></i></a>'
										+'</div>'
									+'</fieldset></div>';
									$obj.html(html);
								}
								break;
							/*case "upload":
								var id = "uploaderBtn" + new Date().getTime();
								html = '<div class="input-group" style="width:100%"> <input name="' + item.member + '" type="hidden" class="' + required + '">';
								if(layui.admin.lang()) {
									html += '<a id="' + id + '" class="btn btn-white btn-sm" ' + disabled + '><i class="fa fa-plus"></i> Add file</a> '
								} else {
									html += '<a id="' + id + '" class="btn btn-white btn-sm" ' + disabled + '><i class="fa fa-plus"></i> 添加文件</a> '
								}
								html += '<div class="fileListDiv"></div></div>';
								$obj.append(html);
								var componentAccept = $obj.attr("component-accept");
								var componentUploadpath = $obj.attr("component-uploadpath");
								myHelper.initWebUploader(id, componentAccept, componentUploadpath);
								break;*/
							default:
								break;
						}
					}
				})
			})
		},
		setFormLang:function(dictionaryId, formObj){
	    	layui.admin.getDictionary('page-form-field', dictionaryId, function (d) {
	    		console.log(d)
	    		$.each(d, function (idx, item) {
	    			var text = item['memberName' + layui.admin.lang()];
	                var $obj = formObj.find('[name="' + item.member + '"]');
	                $obj.closest(".adapt").children("label").text(text);
	                var componentType = $obj.attr("component-type");
	                
	                var required = $obj.hasClass("required") ? "required" : "";
	                var disabled = $obj.attr("disabled");
	                switch (componentType){
	                	case "input":
	                		break;
	                	case "textarea":
	                		break;
	                	case "select":
	                		var componentId = $obj.attr("component-id");
	                		if(componentId){
	                			var loginSecLevel = layui.admin.getSessionList("secLevel");
								if(loginSecLevel){
									loginSecLevel = parseInt(loginSecLevel);
								}
								var thisName = $obj.attr("name");
	                			layui.admin.getDictionary('data-params', componentId, function (res) {
		                			var html = "";
		                			$.each(res, function(index, term) {
		                				if(thisName == "secLevel" && loginSecLevel && parseInt(term.member) > loginSecLevel){
					    					return;
					    				}
		                				html += "<option value='"+term.member+"'>"+term["memberName" + layui.admin.lang()]+"</option>";
		                			});
		                			$obj.append(html);
		                			myHelper.initSelect2($obj);
		                		});
	                		}else{
	                			myHelper.initSelect2($obj);
	                		}
	                		break;
	                	case "checkbox":
	                		var componentId = $obj.attr("component-id");
	                		if(componentId){
	                			layui.admin.getDictionary('data-params', componentId, function (res) {
		                			var html = "";
		                			$.each(res, function(index, term) {
		                				html += "<input type='checkbox' name='"+item.member+"List' value='"+term.member+"' class='i-checks "+required+"'>"+term["memberName" + layui.admin.lang()];
		                			});
		                			$obj.append(html);
		                			myHelper.initIChecks($obj);
		                		});
	                		}else{
	                			myHelper.initIChecks($obj);
	                		}
	                		if(disabled){
	                			myHelper.disabledIchecks($obj);
	                		}
	                		break;
	                	case "radio":
	                		var componentId = $obj.attr("component-id");
	                		if(componentId){
	                			layui.admin.getDictionary('data-params', componentId, function (res) {
		                			var html = "";
		                			$.each(res, function(index, term) {
		                				if(required && index == 0){
		                					html += "<input type='radio' name='"+item.member+"' value='"+term.member+"' class='i-checks' checked='checked'>"+term["memberName" + layui.admin.lang()];
		                				}else{
		                					html += "<input type='radio' name='"+item.member+"' value='"+term.member+"' class='i-checks'>"+term["memberName" + layui.admin.lang()];
		                				}
		                			});
		                			$obj.append(html);
		                			myHelper.initIChecks($obj);
		                		});
	                		}else{
	                			myHelper.initIChecks($obj);
	                		}
	                		if(disabled){
	                			myHelper.disabledIchecks($obj);
	                		}
	                		break;
	                	case "datePicker":
	                		break;
	                	case "treeSelect":
	                		var textName = item.member.replace(".id",".name");
	                		var html = '<input name="'+item.member+'" type="hidden">'+
								'<input name="'+textName+'" readonly="readonly" class="form-control '+required+'" type="text" value="">'+
								'<a class="tag-div-button"><i class="fa fa-search"></i></a>';
							$obj.append(html);
	                		break;
	                	case "iconSelect":
	                		var html = '<input name="'+item.member+'" readonly="readonly" class="form-control '+required+'" type="text" value="">'+
								'<a class="tag-div-button"><i class="fa fa-search"></i></a>';
							$obj.append(html);
	                		break;
	                	case "grid":
	                		var javaField = $obj.attr("javaField");
	                		var textName = item.member.replace(".id","") + "."+ javaField.replace(item.member+"|","");
	                		var html = '<input name="'+item.member+'" type="hidden">'+
                				'<input name="'+textName+'" readonly="readonly" class="form-control '+required+'" type="text" value="">'+
								'<a class="tag-div-button"><i class="fa fa-search"></i></a>';
							$obj.append(html);
	                		break;
	                	case "upload":
	                		var id = "uploaderBtn"+new Date().getTime();
		                	html = '<div class="input-group" style="width:100%"> '+
		                		'<input name="'+item.member+'" type="hidden" class="'+required+'">';
		                	if(layui.admin.lang()){
		                		html += '<a id="'+id+'" class="btn btn-white btn-sm" '+disabled+'><i class="fa fa-plus"></i> Add file</a> ';
		                	}else{
		                		html += '<a id="'+id+'" class="btn btn-white btn-sm" '+disabled+'><i class="fa fa-plus"></i> 添加文件</a> ';
		                	}
			                html +=	'<div class="fileListDiv"></div>'+'</div>';
							$obj.append(html);
							var componentAccept = $obj.attr("component-accept");
							var componentUploadpath = $obj.attr("component-uploadpath");
							myHelper.initWebUploader(id,componentAccept,componentUploadpath);
	                		break;
	                	case "uploadsec":
	                		var id = "uploaderBtn"+new Date().getTime();
		                	html = '<div class="input-group" style="width:100%"> '+
		                		'<input name="'+item.member+'" type="hidden" class="'+required+'">';
		                	if(layui.admin.lang()){
		                		html += '<a id="'+id+'" class="btn btn-white btn-sm" '+disabled+'><i class="fa fa-plus"></i> Add file</a> ';
		                	}else{
		                		html += '<a id="'+id+'" class="btn btn-white btn-sm" '+disabled+'><i class="fa fa-plus"></i> 添加文件</a> ';
		                	}
			                html +=	'<div class="fileListDiv"></div>'+'</div>';
							$obj.append(html);
							var componentAccept = $obj.attr("component-accept");
							var componentUploadpath = $obj.attr("component-uploadpath");
							myHelper.initWebUploader(id,componentAccept,componentUploadpath,"sec");
	                		break;
	                	case "summernote":
							myHelper.initSummernote($obj);
							if(disabled) {
								$obj.summernote("disable");
							}
							break;
	                	default:
	                		break;
	                }
	    		});
			});
	    },
	    getValueFromDictionary:function(dictionaryId,value){
	    	var arr = [];
	    	layui.admin.getDictionary('data-params', dictionaryId, function (res) {
    			$.each(res, function(index, term) {
    				if(term.member == value){
    					arr.push(term["memberName" + layui.admin.lang()]);
    				}
    			});
    		});
			return arr.join("，");
	    },
	    formParamsToObject:function(params){
			var paramArr = params.split('&');
			var res = {};
			for(var i = 0;i<paramArr.length;i++){
				var str = paramArr[i].split('=');
				var regBegin = /^beginD.*/;
				var regEnd = /^endD.*/;
				if(regBegin.test(str[0]) || regEnd.test(str[0])){
					if(str[1] !== ''){
						res[str[0]]=str[1];
					}
				}else{
					res[str[0]]=str[1];
				}
			}
			return res;
		}
    };

    //兼容CommonJs规范
    if (typeof module !== 'undefined' && module.exports) module.exports = helper;

    //兼容AMD/CMD规范
    if (typeof define === 'function') define(function() { return helper; });

    //注册全局变量，兼容直接使用script标签引入该插件
    global.helper = helper;

//this，在浏览器环境指window，在nodejs环境指global
//使用this而不直接用window/global是为了兼容浏览器端和服务端
//将this传进函数体，使全局变量变为局部变量，可缩短函数访问全局变量的时间
})(this);

var myHelper = new helper();