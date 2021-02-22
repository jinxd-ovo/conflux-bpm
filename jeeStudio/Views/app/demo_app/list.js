(function($, doc) {
	var settings = app.getSettings();
	var params = app.getAllUrlParam();
	if(params.path){
		app.path = params.path;
		app.setPath(app.path);
	}else{
		app.path = app.getPath();
	}
	if(params.staticPath){
		app.staticPath = params.staticPath;
		app.setStaticPath(app.staticPath);
	}else{
		app.staticPath = app.getStaticPath();
	}
	if(params.token){
		app.setToken(params.token);
	}
	var headerVm = new Vue({
		el: "#header",
		methods:{
			add:function(){
			  			  	var urlParams = "?id="+"&viewFlag=set"+"&title=新建"+"&formNo="+vm.formNo+"&infoId="+"&btns="+params;
				$.openWindow({
					id: 'content' + new Date().getTime(),
					url: app.staticPath + 'demo_app/content.html' + urlParams,
					show: {
						aniShow: 'pop-in'
					},
					extras: {
						id:'',
						viewFlag: 'set',
						title: '新建',
						formNo: vm.formNo,
					},
					styles: {
						cachemode:"noCache"
					}
				});
			}
		}
	});
	
	var vm = new Vue({
		el: "#vue-dom",
		data: {
			activeChannel: "D",
			rows: [],
			searchName: "",
			nomMore: false,
			ip:"",
			formNo:""
		},
		created: function() {
			this.ip = app.ip;
		},
		mounted:function(){
		},
		updated: function () {
	    },
	    watch: {
	    	//第一种写法 适用于普通变量（ 简单类型的值的观测写法）
			searchName: function(val, oldVal) {
				var _this = this;
				_this.rows = [];
				_this.search();
			}
		},
		methods: {
			reload:function(){
				var _this = this;
			  	app.ajax({
			  		url: app.path + '/app/zform/data/demo_app?path=path&length=0', 
			  		type: 'post', 
			  		data: JSON.stringify({
						s01: _this.searchName
					}),
			  		success: function(result){
						setTimeout(function(){
							_this.rows = result.data.rows;
							_this.formNo = result.data.formNo;
							$("#refreshContainer").pullRefresh().endPulldownToRefresh();
							if(_this.nomMore){
								$('#refreshContainer').pullRefresh().refresh(true);
							}
						},0);
					}
				});
			},
			getList: function(current) {
				var _this = this;
				var arrWithOutUnsent = app.grep(_this.rows,function(item,i){
					return item.unsent != true;
				});
				app.ajax({
					url: app.path + '/app/zform/data/demo_app?path=path&length=' + arrWithOutUnsent.length, 
					type: 'post',
					data: JSON.stringify({
						s01: _this.searchName
					}),
					success: function(result){
						setTimeout(function(){
							_this.rows = _this.rows.concat(result.data.rows);
							$("#refreshContainer").pullRefresh().endPullupToRefresh(result.data.nomMore);
						},0);
					}
				});
			},
			search: function(){
				var _this = this;
				app.ajax({
					url: app.path + '/app/zform/data/demo_app?path=path&length=' + _this.rows.length, 
					type: 'post', 
					data: JSON.stringify({
						s01: _this.searchName
					}),
					success: function(result){
						setTimeout(function(){
							_this.rows = result.data.rows;
							$("#refreshContainer").pullRefresh().endPullupToRefresh(result.data.nomMore);
						},0);
					}
				});
			},
			goToInfo:function(event, row){
				var elem = event.target;
				var li = elem.parentNode.parentNode;
				mui.swipeoutClose(li);
				
				var id = row.id;
				var urlParams = "?id="+"&viewFlag="+"&title=查看"+"&formNo="+vm.formNo+"&infoId="+id;
				$.openWindow({
					id: 'content' + new Date().getTime(),
					url: app.staticPath + 'demo_app/content.html' + urlParams,
					show: {
						aniShow: 'pop-in'
					},
					extras: {
						infoId: id,
						viewFlag: '',
						title: '查看',
						formNo: vm.formNo
					},
					styles: {
						cachemode:"noCache"
					}
				});
			},
			goToEditInfo:function(event, row){
				var elem = event.target;
				var li = elem.parentNode.parentNode;
				mui.swipeoutClose(li);
				
				var id = row.id;
				var urlParams = "?id="+"&viewFlag=set"+"&title=编辑"+"&formNo="+vm.formNo+"&infoId="+id;
				$.openWindow({
					id: 'content' + new Date().getTime(),
					url: app.staticPath + 'demo_app/content.html' + urlParams,
					show: {
						aniShow: 'pop-in'
					},
					extras: {
						infoId: id,
						viewFlag: 'set',
						title: '编辑',
						formNo: vm.formNo
					},
					styles: {
						cachemode:"noCache"
					}
				});
			},
			deletRow: function(event, index, row) {
				var _this = this;
				
				var elem = event.target;
				var li = elem.parentNode.parentNode;
				
				var btnArray = ['确认', '取消'];
				mui.confirm('确认删除该条记录？', '提 示', btnArray, function(e) {
					if (e.index == 0) {
						var isData = {
							id:row.id,
							formNo: vm.formNo
						}
						app.ajax({
							url: app.path + '/app/zform/delete', 
							type: 'post', 
							data: isData, 
							success: function(result){
								setTimeout(function(){
									//删除数组中的元素
									_this.rows.splice(index, 1);
									mui.toast("删除成功");
									mui.swipeoutClose(li);
								},0);
							}
						});
					} else {
						mui.swipeoutClose(li);
					}
				});
			}
		}
	});

	$.init({
		pullRefresh: {
			container: "#refreshContainer",
			down: {
				/*style:'circle',*/
				contentdown : "下拉可以刷新",//可选，在下拉可刷新状态时，下拉刷新控件上显示的标题内容
			    contentover : "释放立即刷新",//可选，在释放可刷新状态时，下拉刷新控件上显示的标题内容
			    contentrefresh : "正在刷新...",
				auto: true, 
				callback: function(){
					vm.reload();
				}
			},
			up: {
				auto: false,
				contentrefresh: "正在加载...", 
				contentnomore: '没有更多数据了', 
				callback: function() {
					vm.getList();
				} 
			}
		}
	});
	
	 
	//本段代码用于返回到指定页面
	mui.back = function(){
	    $.openWindow({
			id: 'work' + new Date().getTime(),
			url: '/work.html',
			show: {
				aniShow: 'pop-in'
			},
			styles: {
				cachemode:"noCache"
			}
		});
	};
	
	$.plusReady(function() {
		/*
		本段代码用于直接退出App
		var backButtonPress = 0;
		$.back = function(event) {
			backButtonPress++;
			if (backButtonPress > 1) {
				plus.runtime.quit();
			} else {
				plus.nativeUI.toast('再按一次退出应用');
			}
			setTimeout(function() {
				backButtonPress = 0;
			}, 1000);
			return false;
		};
		*/
	});
}(mui, document));