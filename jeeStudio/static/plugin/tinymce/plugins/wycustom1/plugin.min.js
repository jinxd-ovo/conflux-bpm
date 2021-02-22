tinymce.PluginManager.add('wycustom1', function(editor, url) {
	var doSoming = function() {
		var html = editor.getContent();
		$html = $("<div>").append(html);
		$html.find("#report-table").toggleClass("border");
		editor.setContent($html.html());
		editor.save();
	};

	// 注册一个工具栏按钮名称
	editor.ui.registry.addButton('wycustom1', {
		text: '添加/删除报表边框',
		onAction: function() {
			doSoming();
		}
	});

	// 注册一个菜单项名称 menu/menubar
	editor.ui.registry.addMenuItem('wycustom1', {
		text: '添加/删除报表边框',
		onAction: function() {
			doSoming();
		}
	});

//	return {
//		getMetadata: function() {
//			return {
//				//插件名和链接会显示在“帮助”→“插件”→“已安装的插件”中
//				name: "wycustom1 plugin", //插件名称
//				url: "http://wycustom1plugindocsurl.com", //作者网址
//			};
//		}
//	};
});