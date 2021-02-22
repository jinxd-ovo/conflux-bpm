;(function(global) {

    //开启严格模式，规范代码，提高浏览器运行效率
    "use strict";

    //定义一个类，通常首字母大写
    var jsTreeUtil = function(getTime, opt) {
    	var _this = this;
    	_this.body = $("#body-div" + getTime);
    	_this.dom = _this.body.find("#carKindjsTree");
    	_this.to = false;
    	_this.jstreeData = {};
    	_this.config = {  
			'core': {
				"multiple": false,
				"animation": 0,
				"themes": {
					"variant": "large",
					"icons": false,
					"stripes": false
				},
				'data': function(obj, callback) {
					//此处需要修改成新框架请求
					LayerUtil.ajax({
						url: opt.url,
						type: opt.type ? opt.type : 'post',
            			contentType: "application/json",
						loading: false
						, tipOk: false,
		        		success: function(res){
		        			if (res.code === 0) {
								var _data = res.data[opt.actionDataName];
								var _obj = [];
								for(var i=0;i<_data.length;i++){
									if(_data[i].parentId == null){
										_data[i].parentId = 0;
									}
									if(_data[i].parentId == 0){
										_obj.push({
											id: _data[i].id,
											parent: '#',
											text: _data[i]["name"+layui.admin.lang()],
											parentId: _data[i].parentId
										})
									}else{
										_obj.push({
											id: _data[i].id,
											parent: _data[i].parentId,
											text: _data[i]["name"+layui.admin.lang()],
											parentId: _data[i].parentId
										})
									}
								}
								_this.jstreeData = _obj;
								callback.call(this, _obj);
							}
		        		}
		        	})
				}
			},
			"conditionalselect": function(node, event) {
				return false;
			},
			/* 'plugins': ['types', 'wholerow', "search"], */
			'plugins': ['wholerow', "search"]
		};
    };

    //覆写原型链，给继承者提供方法
    jsTreeUtil.prototype = {
        init: function(fuc,name, openAll) {
        	var _this = this;
        	
        	_this.dom.jstree(_this.config).bind("activate_node.jstree", function(obj, e) {
        		// 或者e.node.id
				var node = _this.dom.jstree(true).get_selected(true)[0];
				var opt = {
					silent: true,
					query: {
						'kind.id': node.id
					}
				};
				_this.body.find(".layout-tab>li>a").removeClass("active");
				_this.body.find(".layout-tab>li>a").eq(0).addClass("active");
				
				if(typeof fuc == "function"){
					fuc(node);
				}
        	}).on('loaded.jstree', function(e,data) {
				//_this.dom.jstree('open_all');
				if(openAll){
					_this.dom.jstree().open_all();
				}
			}).on('ready.jstree', function() {
				$.each(_this.jstreeData, function(i, item) {
					//var id = "#carKindjsTree li#" + item.id + ">.jstree-wholerow";
					var $id = _this.dom.find("li#" + item.id + ">.jstree-wholerow")
					if ($id.find(".jsTreeNumDiv").length > 0) {
						$id.find(".jsTreeNumDiv").remove();
					}
					// 添加数字
					$id.append('<div class="jsTreeNumDiv">' + '</div>');//data[0].num +
				});
				
				var selectId = sessionStorage.getItem("home-list-targetId");
				if(selectId){
					_this.dom.find("li#"+selectId).find("div.jstree-wholerow").trigger("click")
				}
			}).on('after_open.jstree', function(node, obj) {
				$.each(obj.node.children_d, function(i, item) {
					//var id = "#carKindjsTree li#" + item + ">.jstree-wholerow";
					var $id = _this.dom.find("li#" + item + ">.jstree-wholerow")
					if ($id.find(".jsTreeNumDiv").length > 0) {
						$id.find(".jsTreeNumDiv").remove();
					}
					var data = $.grep(_this.jstreeData, function(obj, i) {
						return obj.id == item;
					})
					if (data.length > 0) {
						$id.append('<div class="jsTreeNumDiv">' + '</div>');//data[0].num +
					}
				});
			});
        	
        	if(name){
        		_this.body.find("#getAll span").text(name);
        	}
        	
        	//绑定查询事件
        	_this.body.find('#search-jstree-input').keyup(function() {
				if (_this.to) {
					clearTimeout(_this.to);
				}
				_this.to = setTimeout(function() {
					var v = _this.body.find('#search-jstree-input').val();
					_this.body.find('#carKindjsTree').jstree(true).search(v);
				}, 250);
			});
        	
			//全部类型
			_this.body.find("#getAll").on("click", function() {
				_this.body.find('.jstree-wholerow').removeClass('jstree-wholerow-clicked');
				
				var node = {
					id:0,
					text:""
				}
				if(typeof fuc == "function"){
					fuc(node);
				}
			});
        	
			return "";
        }
    };

    //兼容CommonJs规范
    if (typeof module !== 'undefined' && module.exports) module.exports = jsTreeUtil;

    //兼容AMD/CMD规范
    if (typeof define === 'function') define(function() { return jsTreeUtil; });

    //注册全局变量，兼容直接使用script标签引入该插件
    global.JsTreeUtil = jsTreeUtil;

//this，在浏览器环境指window，在nodejs环境指global
//使用this而不直接用window/global是为了兼容浏览器端和服务端
//将this传进函数体，使全局变量变为局部变量，可缩短函数访问全局变量的时间
})(this);

