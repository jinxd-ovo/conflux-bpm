;(function(global) {

    //开启严格模式，规范代码，提高浏览器运行效率
    "use strict";

    //定义一个类，通常首字母大写
    var Summernote = function(id,option) {
    	var _this = this;
    	_this.dom = $("#"+id);
    	var config = {  
			height: 200,
			lang: 'zh-CN',
			toolbar: [
			    ['view', ['codeview']],
			    ['style', ['style']],
			    ['font',['bold', 'italic', 'underline', 'clear']],
			    ['font', ['strikethrough', 'superscript', 'subscript']],
			    ['fontname', ['fontname']],
			    ['fontsize', ['fontsize']],
			    ['color', ['color']],
			    ['para', ['ul', 'ol', 'paragraph']],
			    ['table', ['table']],
			    ['insert', ['link', 'picture', 'video']],
				['height', ['height']]
		  	],
		  	fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New',"微软雅黑","宋体","黑体"]
		};
		_this.totalConfig = $.extend(true, config, option);
    };

    //覆写原型链，给继承者提供方法
    Summernote.prototype = {
        init: function() {
        	return this.dom.summernote(this.totalConfig);
        }
    };

    //兼容CommonJs规范
    if (typeof module !== 'undefined' && module.exports) module.exports = Summernote;

    //兼容AMD/CMD规范
    if (typeof define === 'function') define(function() { return Summernote; });

    //注册全局变量，兼容直接使用script标签引入该插件
    global.SummernoteUtil = Summernote;

//this，在浏览器环境指window，在nodejs环境指global
//使用this而不直接用window/global是为了兼容浏览器端和服务端
//将this传进函数体，使全局变量变为局部变量，可缩短函数访问全局变量的时间
})(this);
