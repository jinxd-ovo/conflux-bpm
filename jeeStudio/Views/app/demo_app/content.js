(function($, doc) {
	var self = app.getAllUrlParam();
	if(self.path){
		app.path = self.path;
		app.setPath(app.path);
	}else{
		app.path = app.getPath();
	}
	if(self.staticPath){
		app.staticPath = self.staticPath;
		app.setStaticPath(app.staticPath);
	}else{
		app.staticPath = app.getStaticPath();
	}
	if(self.token){
		app.setToken(params.token);
	}
	var needDo = self.needDo;
	var countMounted = 0;
	function allMounted(vmName){
		if(countMounted == 5){
			var title = self.title;
			headerVm.title = title;
			var viewFlag = self.viewFlag;
			bottomVm.viewFlag = viewFlag;
			vm.viewFlag = viewFlag;
			vm.formNo = self.formNo;
			vm.data.formNo = self.formNo;
			if(self.btns){
				var btns = [];
				for(var i=0; i<self.btns.split(";").length; i++){
					var btn = {};
					for(var j=0; j<self.btns.split(";")[i].split(",").length; j++){
						btn[self.btns.split(";")[i].split(",")[j].split(":")[0]] = self.btns.split(";")[i].split(",")[j].split(":")[1];
					}
					if(Object.keys(btn).length != 1){
						btns.push(btn);
					}
				}
				bottomVm.btns = btns;
			}
			var dictParams = ",yes_no,yes_no,yes_no";
			if (dictParams == "") dictParams = ",";
			app.ajax({
				url: app.path + '/app/getDict?dictParams=' + dictParams,
				type: 'post',
				success: function(dictList) {
					vm.dict = dictList.data.data;
					var id = self.infoId;
					if(id && id.length > 0) {
						vm.getDetail(id,self.unsent);
					}
				}
			});
		}
	}
	var settings = app.getSettings();
	var headerVm = new Vue({
		el: "#header",
		data: {
			title: ""
		},
		mounted: function() {
			countMounted ++;
			allMounted();
		}
	});
	var bottomVm = new Vue({
		el: "#bottom-tab",
		data: {
			viewFlag: 'set',
			btns: []
		},
		mounted: function() {
			countMounted ++;
			allMounted();
		},
		methods: {
			save: function() {
				var has = true;
				$(".required").each(function(index, item) {
					if(this.value.length == 0) {
						mui.toast("有必填项没有填写");
						setTimeout(function() {
							item.focus();
						}, 1000);
						has = false;
						return false;
					}
				});
				if(has) {
					vm.save();
				}
			}
		}
	});
	var offCanvasSideVm = new Vue({ //
		el: "#offCanvasSide",
		data: {
			treeId: '0',
			title: '',
			filteruser01: '',
			expandedKeysuser01: [],
			treeDatauser01: [],
			filterusers01: '',
			expandedKeysusers01: [],
			treeDatausers01: [],
			filteroffice01: '',
			expandedKeysoffice01: [],
			treeDataoffice01: [],
			filterarea01: '',
			expandedKeysarea01: [],
			treeDataarea01: [],
			defaultProps: {
				children: 'children',
				label: 'text'
			},
			defaultPropsArea: {
				children: 'children',
				label: 'text',
				isLeaf: 'isLeaf'
			}
		},
		mounted: function() {
			countMounted ++;
			allMounted();
		},
		created: function() {
			this.getTreeData();
		},
		updated: function() {
		},
		computed: {
			treeIdComputed: function () {
		      	return this.treeId.indexOf('single');
		    },
			// 模糊搜索
	
		},
		watch: {
			filteruser01: function(val) {
				this.$refs.treeuser01.filter(val);
			},
			filterusers01: function(val) {
				this.$refs.treeusers01.filter(val);
			},
			filteroffice01: function(val) {
				this.$refs.treeoffice01.filter(val);
			},
			filterarea01: function(val) {
				this.$refs.treearea01.filter(val);
			},
		},
		methods: {
			filterNode: function(value, data) {
				if(!value) return true;
				return(data.text.indexOf(value) !== -1 && typeof data.children == 'undefined') || (data.pinyin.indexOf(value) !== -1 && typeof data.children == 'undefined');
			},
			ok: function() {
				if(this.treeId == 'nan') {
				}
				else if(this.treeId == 'users01Tree') {
					var checklist = this.$refs.treeusers01.getCheckedNodes();
					var ids = [];
					var names = [];
					for(var i = 0; i < checklist.length; i++) {
						if(!checklist[i].children || checklist[i].children.length == 0) {
							ids.push(checklist[i].id);
							var textRe = checklist[i].text.replace(/\s+/g, ",");
							var textArray = textRe.split(",")[0];
							names.push(textArray);
						}
					}
					vm.data.users01.id = ids.join(",");
					vm.data.users01.name = names.join(",");
				}
				var offCanvasWrapper = mui('#offCanvasWrapper');
				offCanvasWrapper.offCanvas('close');
			},
			close: function() {
				var offCanvasWrapper = mui('#offCanvasWrapper');
				offCanvasWrapper.offCanvas('close');
			},
			handleNodeClickuser01: function(treeData) {
				if(!treeData.disabled && (!treeData.children || treeData.children.length == 0)) {
					vm.data.user01.id = treeData.id;
					var textRe = treeData.text.replace(/\s+/g, ",");
					var textArray = textRe.split(",")[0];
					vm.data.user01.name = textArray;
					offCanvasSideVm.close();
				}
			},
			handleNodeClickoffice01: function(treeData) {
				if(!treeData.disabled && (!treeData.children || treeData.children.length == 0)) {
					vm.data.office01.id = treeData.id;
					var textRe = treeData.text.replace(/\s+/g, ",");
					var textArray = textRe.split(",")[0];
					vm.data.office01.name = textArray;
					offCanvasSideVm.close();
				}
			},
			handleNodeClickarea01: function(treeData) {
				if(!treeData.hasChildren) {
					vm.data.area01.id = treeData.id;
					vm.data.area01.name = treeData.text;
					offCanvasSideVm.close();
				}
			},	
			sortElTreeData(data,pObj){
				var _this = this;
				if(!pObj){
					pObj = {id:"0"};
				}
				var pList = data.filter(function(item,i){
					return item.parentId == pObj.id;
				})
				pObj.children = pList;
				if(pList.length>0){
					pList.forEach(function(pItem,i){
						var sonList = data.filter(function(sItem,j){
							return sItem.parentId == pItem.id;
						})
						pItem.children = sonList;
						if(sonList.length>0){
							sonList.forEach(function(sItem,j){
								if(sItem.hasChildren){
									_this.sortElTreeData(data,sItem);
								}
							})
						}
					})
				}
				return pList;
			},
			getTreeData: function() {
				var _this = this;
				app.ajax({
					url: app.path + '/sys/tagTree/userTree',
					type: 'post',
					data: '',
					success: function(result) {
						setTimeout(function() {
							var data = result.data.data;
							_this.setPinyin(data);
							data = _this.sortElTreeData(data);
							_this.treeDatauser01 = data;
//							data = _this.sortElTreeData(data);
							_this.treeDatausers01 = data;
						}, 0);
					}
				});
				
				app.ajax({
					url: app.path + '/sys/tagTree/officeTree', 
					type: 'post',
					data: '',
					success: function(result) {
						setTimeout(function() {
							var data = result.data.data;
							_this.setPinyin(data);
							data = _this.sortElTreeData(data);
							_this.treeDataoffice01 = data;
						}, 0);
					}
				});
				app.ajax({
					url: app.path + '/sys/tagTree/areaTreeAsync?id=0',
					type: 'post',
					data: '',
					success: function(result) {
						setTimeout(function() {
							var data = result.data.data;
							data = _this.sortElTreeData(data);
							_this.treeDataarea01 = data;
						}, 0);
					}
				});
			},
			loadNode:function(node, resolve){
				var id = 0;
				if(node.data && node.data.id){
					id = node.data.id;
				}
				app.ajax({
					url: app.path + '/sys/tagTree/areaTreeAsync?id='+id,
					type: 'post',
					data: '',
					success: function(result) {
						setTimeout(function() {
							var data = result.data.data;
							
							data.forEach(function(item,i){
								item.isLeaf = !item.hasChildren;
								item.text = item.name;
							})
							resolve(data);
						}, 0);
					}
				});
			},
			setPinyin: function(data) {
				var _this = this;
				for(var i = 0; i < data.length; i++) {
					data[i].text = data[i].name;
					var name = data[i].text.split(" ")[0];
					var pinyin = ConvertPinyin(name);
					var frist = ConvertToFristPinyin(name);
					data[i].pinyin = pinyin + ' ' + frist;
					if(data[i].children && data[i].children.length > 0) {
						_this.setPinyin(data[i].children);
					}
				}
			},
			getAreaData:function(result,key){
				var _this = this;
				var arr = app.grep(result,function(item,i){
					return item.pId == key;
				});
				$.each(arr, function(index,obj) {
					obj.text = obj.name;
					obj.children = app.grep(result,function(item,i){
						return item.pId == obj.id;
					});
					if(obj.children.length > 0){
						$.each(obj.children, function(i,item) {
							item.text = item.name;
							item.children = _this.getAreaData(result,item.id);
						});
					}
				});
				return arr;	
			},
		}
	});
	var vm = new Vue({
		el: "#vue-dom",
		data: {
			histoicFlowList:[],
			data: {
				c01: [],
				user01: {
					id: '',
					name: ''
				},
				users01: {
					id: '',
					name: ''
				},
				office01: {
					id: '',
					name: ''
				},
				area01: {
					id: '',
					name: ''
				},
				act:{
					taskId: '',
					taskName: '',
					taskDefKey: '',
					procInsId: '',
					procDefKey: '',
					procDefId: '',
					param: '',
					flag: '',
					comment: ''
				},
				ruleArgs: {
					form: {}
				},
				formNo: '',
			},
			viewFlag: 'set',
			dict: {},
			formNo: '',
			files: [],//每次选择图片		
		},
		created: function() {
			//			this.getDetail();
		},
		mounted: function() {
			countMounted ++;
			allMounted();
			
			this.$nextTick(function(){
			})
		},
		updated: function() {},
		methods: {
			initWebUploader: function(ref,vmKey,vmUploadfilesKey) {
				var _this = this;
				var $ = jQuery;
				var groupId = "";
				var flie_count = 0;
				var cSize = 1 * 1024 * 1024;
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
				
				var uploadFileSize = 50485760;
				var componentUploadpath = '/system/sysFile/fileUploadBatchProgress'
				
				var uploader = WebUploader.create({
					duplicate: true,
					auto: true,
					swf: 'static/plugin/webuploader-0.1.5/Uploader.swf',
					server: app.path + componentUploadpath,
					pick: {
						id: ref
					},
					formData: {
						groupId: groupId,
						chunk: flie_count
					},
					fileSingleSizeLimit: uploadFileSize,
					threads: 1,
					chunked: true,
					chunkSize: cSize,
					resize: false,
					accept: {}
				});
				uploader.onError = function(code) {
					var max = uploadFileSize;
					if(max/1024/1024 < 1){
						max = (max / 1024 ) + "K！";
					}else{
						max = (max / 1024 / 1024) + "M！";
					}
					if(code == "F_EXCEED_SIZE") {
						code = text1 + max
					} else if(code == "Q_EXCEED_SIZE_LIMIT") {
						code = text1 + max
					} else if(code == "Q_TYPE_DENIED") {
						code = text2 + "gif,jpg,jpeg,png,bmp,tiff,tif,GIF,JPG,JPEG,PNG,BMP,TIFF,TIF！" + text7
					}
					LayerUtil.error(code)
				};
				uploader.on('uploadBeforeSend', function(obj, data, headers) {
					headers.token = app.getToken();
					groupId = vm.data[vmKey];
					if(groupId == "" || typeof groupId == "undefined") {
						groupId = WebUploader.Base.guid();
						vm.data[vmKey] = groupId;
					}
					data.groupId = groupId;
					mui.showLoading("图片上传中");
				});
				uploader.on('fileQueued', function(file) {
				});
				uploader.on('uploadProgress', function(file, percentage) {
				});
				uploader.on('uploadSuccess', function(file, result) {
					if(result.token) {
						app.setToken(result.token)
					}
					app.ajax({
				  		url: app.path + componentUploadpath + "?groupId=" + groupId + "&fileName=" + file.name + "&fileSize=" + file.size + "&chunk=" + Math.ceil(file.size / (cSize)) + "&isChunk=false&isEncoding=false&cSize=" + cSize,
				  		type: 'post', 
				  		success: function(result) {
							vm.data[vmKey] = result.data.map.groupId;
							uploader.options.formData.groupId = result.data.map.groupId;
							$.each(result.data.map.successList, function(index, data) {
								vm.data[vmUploadfilesKey].remainNum = vm.data[vmUploadfilesKey].remainNum - 1;
								var imgObj = {
									status: "true",
									src: app.path + "/system/sysFile/fileDownload?fileId=" + data.id,
									image: data.name,
									id:data.id
								};
								vm.data[vmUploadfilesKey].imgList.push(imgObj);
							});
							mui.previewImage();
							mui.hideLoading();
						}
					})
					
				});
				uploader.on('uploadComplete', function(file) {
					flie_count = 0
				})
			},
			getType:function(src){
				if(/\.png|\.jpg$/i.test(src)){
					return 1;
				}else if(/\.mp4|\.mp3|\.mov$/i.test(src)){
					return 2;
				}else if(/\.doc|\.docx|\.xls|\.xlsx|\.rar|\.zip|\.rtf|\.txt|\.ppt|\.pptx|\.pdf$/i.test(src)){
					return 3;
				}
			},
			chooseImage: function(groupIdKey,uploadfileKey) {
				var btnArray = [{
					title: "拍照"
				}, {
					title: "录像"
				}, {
					title: "从手机相册选择"
				}];
				plus.nativeUI.actionSheet({
					cancel: "取消",
					buttons: btnArray
				}, function(event) {
					var index = event.index;
					switch (index) {
						case 1:
							vm.getImage(groupIdKey,uploadfileKey);
							break;
						case 2:
							vm.getVideo(groupIdKey,uploadfileKey);
							break;
						case 3:
							vm.galleryImgOrVideo(groupIdKey,uploadfileKey);
							break;
						case 4:
							vm.glanceOver(groupIdKey,uploadfileKey);
							break;
					}
				});
			},
			getVideo: function(groupIdKey,uploadfileKey){
				var _this = this;
				var camera = plus.camera.getCamera();  
				camera.startVideoCapture(function(path) {  
						vm.saveInGallery(path);
						plus.io.resolveLocalFileSystemURL(path, function(entry) { 
							_this.files.push(entry.toLocalURL());
							vm.appendImage(groupIdKey,uploadfileKey);
						}, function(e) {  
							mui.toast('读取拍照文件错误：' + e.message);  
						});
					},  
					function(error) {
						console.log("取消拍照");
					}, 
					{
						filename:'_doc/camera/',
						index:1,
					}
				); 
			},
			getImage: function(groupIdKey,uploadfileKey){
				var _this = this;
				var camera = plus.camera.getCamera();  
				camera.captureImage(function(path) {  
						console.log('保存照片到系统相册');
						vm.saveInGallery(path);
						console.log("读取拍照文件");
						plus.io.resolveLocalFileSystemURL(path, function(entry) { 
							_this.files.push(entry.toLocalURL());
							vm.appendImage(groupIdKey,uploadfileKey);
						}, function(e) {  
							mui.toast('读取拍照文件错误：' + e.message);  
						});
					},  
					function(error) {
						console.log("取消拍照");
					}, 
					{
						filename:'_doc/camera/',
						index:1,
					}
				); 
			},
			saveInGallery: function(path) {  
				plus.gallery.save(path, function(){
					console.log('保存成功');
				}, function(e){
					console.log('保存失败: '+JSON.stringify(e));
				});
			},
			//从相册中选择图片
			galleryImgOrVideo: function(groupIdKey,uploadfileKey){
				var _this = this;
				plus.gallery.pick(function(e) {  
					for(var i in e.files) {
						_this.files.push(e.files[i]);
					}
					vm.appendImage(groupIdKey,uploadfileKey);  
				}, function(e) {  
					console.log("取消选择图片");
				}, {  
					filter: "none",  //image，video，none 
					multiple: true,  
					system: false,  
					maximum: _this.data[uploadfileKey].remainNum,
					onmaxed: function() {  
						plus.nativeUI.toast('最多可以选择'+ _this.data[uploadfileKey].remainNum +'张图片');
					}
				});  
			},
			appendImage: function(groupIdKey,uploadfileKey){
				var _this = this;
				
				//异步上传图片
				_this.data[groupIdKey] = (_this.data[groupIdKey] == null || _this.data[groupIdKey] == "") ? vm.getUUID() : _this.data[groupIdKey];//分组
				//显示上传图片
				for(var i=0;i<_this.files.length;i++){
					//修改剩余数量
					_this.data[uploadfileKey].remainNum = _this.data[uploadfileKey].remainNum - 1;
					//var wt = plus.nativeUI.showWaiting();
					var task = plus.uploader.createUpload(app.path+"app/fileUploadBatch",//
						{method:'POST'},
						function(upload, status){ //上传完成
							//plus.nativeUI.closeWaiting();
							if ( upload.state == 4 && status == 200 ) {
								$.each(_this.data[uploadfileKey].imgList, function(i, obj) {
									if(obj.__UUID__  == upload.__UUID__){
										obj.status = "true";
										_this.$forceUpdate();
									}
								});
							}else{
								/*_this.data[uploadfileKey].imgList = app.grep(_this.data[uploadfileKey].imgList,function(obj, i){
									return obj.__UUID__  == upload.__UUID__;
								});
								_this.data[uploadfileKey].remainNum = _this.data[uploadfileKey].imgList.length;*/
								$.each(_this.data[uploadfileKey].imgList, function(i, obj) {
									if(obj.__UUID__  == upload.__UUID__){
										obj.status = "false";
										_this.$forceUpdate(); 
									}
								});
								
								mui.toast('上传失败，请重试');
								//wt.close();
							}
						}
					);
					task.addData('groupId', _this.data[groupIdKey]);
					task.addFile(_this.files[i], {key:("uploadkey"+i)});
					task.start();
					
					//添加图片对象到数组
					task.image = _this.files[i];
					_this.data[uploadfileKey].imgList.push(task);
					
				}
				//清空本次上传图片
				_this.files = [];
			},
			getUUID: function() {
				return 'xxxx-xxxx-xxxx-xxxx-xxxx'.replace(/[xy]/g, function(c) {
					var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
					return v.toString(16);
				});
			},
			previewImage: function(index,uploadfileKey){
				var _this = this;
				var files = [];
				$.each(_this.data[uploadfileKey].imgList, function(i, obj) {
					if(_this.getType(obj.image) === 1){
						files.push(obj.image);
					}
				});
				plus.nativeUI.previewImage(files,{  
                    current:index,  
                    loop:true  
                }); 
			},
			previewVideo:function(file){
				vp.videoIsPlay = true; 
				vp.src = file;
				/*
				setTimeout(function(){
					vp.player = new plus.video.VideoPlayer('videoCreatDiv',{
						src:file,
						width: '100%',
						height: '100%',
						autoplay: true,
						'show-center-play-btn': false,
						'show-fullscreen-btn': false
					}); 
				},300)
				*/
			},
			removeImage: function(index,uploadfileKey,imageObj){
				var _this = this;
				app.ajax({
					url: app.path + '/system/sysFile/deleteFile?fileId='+imageObj.id,
					type: 'get',
					success: function(result){
						_this.data[uploadfileKey].imgList.splice(index, 1);
						_this.data[uploadfileKey].remainNum += 1;
					}
				})
			},
			reloadUpload: function(imageObj,groupIdKey,uploadfileKey){
				var _this = this;
				
				imageObj.status = '';
				_this.$forceUpdate();
									
				//异步上传图片
				_this.data[groupIdKey] = (_this.data[groupIdKey] == null || _this.data[groupIdKey] == "") ? vm.getUUID() : _this.data[groupIdKey];//分组
				var task = plus.uploader.createUpload(app.path+"app/fileUploadBatch",
					{method:'POST'},
					function(upload, status){ //上传完成
						//plus.nativeUI.closeWaiting();
						if ( upload.state == 4 && status == 200 ) {
							$.each(_this.data[uploadfileKey].imgList, function(i, obj) {
								if(obj.__UUID__  == imageObj.__UUID__){
									obj.status = "true";
									_this.$forceUpdate();
								}
							});
						}else{
							mui.toast('上传失败，请重试');
							//wt.close();
						}
					}
				);
				//显示上传图片
				task.addData('groupId', _this.data[groupIdKey]);
				task.addFile(imageObj.image, {key:("uploadkey")});
				task.start();
			},
			
			open: function(type, title, keys) {
				offCanvasSideVm.title = title;
				offCanvasSideVm.treeId = type;
				offCanvasSideVm.filteruser01 = '';
				offCanvasSideVm.filterusers01 = '';
				offCanvasSideVm.filteroffice01 = '';
				offCanvasSideVm.filterarea01 = '';
				if(type == 'nan') {
				}
				else {
					offCanvasSideVm.expandedKeysuser01 = keys.split(",");
					offCanvasSideVm.expandedKeysusers01 = keys.split(",");
					offCanvasSideVm.$refs.treeusers01.setCheckedKeys(keys.split(","));
					offCanvasSideVm.expandedKeysoffice01 = keys.split(",");
					offCanvasSideVm.expandedKeysarea01 = keys.split(",");
				}
				mui('#offCanvasWrapper').offCanvas('show');
			},
			getDetail: function(id,unsentFlag) {
				var _this = this;
				var data = {
					id: id,
					formNo: vm.formNo,
					procDefKey: '' //?
				}
			  	app.ajax({
			  		url: app.path + '/app/zform/get?id=' + id + '&formNo=' + vm.formNo + '&procDefKey=' ,
			  		type: 'post', 
			  		success: function(result) {
						setTimeout(function() {
							_this.data = $.extend(true, _this.data, result.data.data);
							
							for (var key in _this.data) {
								if(key.indexOf("c0") != -1 || key.indexOf("c1") != -1){
									if(_this.data[key] && typeof _this.data[key] == "string"){
										_this.data[key] = _this.data[key].split(",");
									}else{
										_this.data[key] = [];
									}
								}
								else if(key.indexOf("d0") != -1 || key.indexOf("d1") != -1){
									_this.data[key] = _this.formatterDate(_this.data[key],"date");
									//日期格式根据需要自定义：空 年月日时分秒，date 年月日，month 年月，year 年
								}
								else if(key.indexOf("g0") != -1 && _this.data[key] == null){
									_this.data[key] = {
										id: "",
										s01: ""
									}
								}
								else if(key.indexOf("user0") != -1 && _this.data[key] == null){
									_this.data[key] = {
										id: "",
										name: ""
									}
								}
								else if(key.indexOf("users0") != -1 && _this.data[key] == null){
									_this.data[key] = {
										id: "",
										name: ""
									}
								}
								else if(key.indexOf("office0") != -1 && _this.data[key] == null){
									_this.data[key] = {
										id: "",
										name: ""
									}
								}
								else if(key.indexOf("area0") != -1 && _this.data[key] == null){
									_this.data[key] = {
										id: "",
										name: ""
									}
								}
							}
							_this.data.dirtyUpdateDate = _this.data.updateDate;
						    _this.$forceUpdate();
						}, 0);
					}
				});
			},
			formatterDate:function(str,type){
				if(!str){
					return "";
				}
				var now=new Date(str);
				var year=now.getFullYear(); 
			    var month=now.getMonth()+1; 
			    var date=now.getDate(); 
			    var hour=now.getHours() + 1;; 
			    if(hour<10){
			    	hour = "0"+hour;
			    }
			    var minute=now.getMinutes(); 
			    var second=now.getSeconds(); 
				if(type == ""){
			   		return year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second; 
				}else if(type == "date"){
			    	return year+"-"+month+"-"+date; 
				}else if(type == "year"){
			    	return year; 
				}else if(type == "month"){
			    	return year+"-"+month; 
				}
			},
			getValueByDict: function(dictKey, key) {
				if(key == null || key == "") {
					return "";
				}
				var grep = app.grep(this.dict[dictKey], function(obj, index) {
					return obj.member == key;
				});
				if(grep != null && grep.length > 0) {
					return grep[0].memberName;
				} else {
					return "";
				}
			},
			getValueByDictForCheckbox: function(dictKey, key) {
				if(key == null || key == "") {
					return "";
				}
				var arr = [];
				for(var i=0; i<key.length; i++){
					var grep = app.grep(this.dict[dictKey], function(obj, index) {
						return obj.member == key[i];
					});
					if(grep != null && grep.length > 0) {
						arr.push(grep[0].memberName);
					}
				}
				return arr.join(",");
			},
			save: function() {
				this.data.formNo = vm.formNo;
				var submitData = {};
				for (var key in this.data) {
					if(/^c((0[1-9]{1})|(10))List$/.test(key)) {
						delete this.data[key];
					}
					if(Array.isArray(this.data[key])){
						submitData[key] = this.data[key].join(",");
					}else{
						submitData[key] = this.data[key];
					}
				}
				if(needDo=="needDo" || needDo=='sent' || needDo=='done'){
					app.ajax({
						url: app.path + '/app/zform/save', 
						type: 'post', 
						data: JSON.stringify(submitData), 
						success: function(result) {
							mui.toast(result.msg);
							$.openWindow({
								id: 'list' + new Date().getTime(),
								url: '/page/' + needDo + '.html',
								show: {
									aniShow: 'pop-in'
								},
								styles: {
									cachemode: "noCache"
								}
							});
						}
					});
				}else{
					app.ajax({
						url: app.path + '/app/zform/save', 
						type: 'post', 
						data: JSON.stringify(submitData), 
						success: function(result) {
							mui.toast(result.msg);
							$.openWindow({
								id: 'list' + new Date().getTime(),
								url: app.staticPath + 'demo_app/list.html',
								show: {
									aniShow: 'pop-in'
								},
								styles: {
									cachemode: "noCache"
								}
							});
						}
					});
				}
				
			},
			datePicker: function(event, data) {
				var _this = this;
				var _self = event.currentTarget;
				var model = _self.getAttribute('data-model');
				if(_self.picker) {
					_self.picker.show(function(rs) {
						vm.data[model] = rs.text;
						_self.picker.dispose();
						_self.picker = null;
						_this.$forceUpdate();
					});
				} else {
					var optionsJson = _self.getAttribute('data-options') || '{}';
					var options = JSON.parse(optionsJson);
					_self.picker = new mui.DtPicker(options);
					_self.picker.show(function(rs) {
						vm.data[model] = rs.text;
						_self.picker.dispose();
						_self.picker = null;
						_this.$forceUpdate();
					});
				}
			}
		}
	});
	
	
	var vp = new Vue({
		el: "#videoPlayDiv",
		data: {
			video:'',
			videoIsPlay:false,
			src:''
		},
		mounted: function() {
			countMounted ++;
			allMounted();
		},
		methods: {
			closeVideo:function(){
				vp.videoIsPlay = false;
				//vp.player.close();
			},
		}
	});

	mui('.mui-scroll-wrapper').scroll({
		deceleration: 0.0005 //flick 减速系数，系数越大，滚动速度越慢，滚动距离越小，默认值0.0006
	});
}(mui, document));