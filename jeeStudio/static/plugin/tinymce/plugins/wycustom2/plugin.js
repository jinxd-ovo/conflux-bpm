tinymce.PluginManager.add('wycustom2', function(editor, url) {
	var doSoming = function() {
//		var html = editor.getContent();
//		$html = $("<div>").append(html);
//		$html.find("#report-table").removeClass("red");
//		$html.find("#report-table").removeClass("black");
//		$html.find("#report-table").removeClass("green");;
//		$html.find("#report-table").addClass("red");
//		editor.setContent($html.html());
//		editor.save();
		return editor.windowManager.open({
	      title: '设置表格宽度',
	      body: {
	        type: 'panel',
	        items: [
	          {
	            type: 'input',
	            name: 'title',
	            label: '请以“px”或“%”结尾'
	          }
	        ]
	      },
	      buttons: [
	        {
	          type: 'cancel',
	          text: 'Close'
	        },
	        {
	          type: 'submit',
	          text: 'Save',
	          primary: true
	        }
	      ],
	      onSubmit: function (api) {
	        var data = api.getData();
	        // 将输入框内容插入到内容区光标位置
	        var html = editor.getContent();
			$html = $("<div>").append(html);
			$html.find("#report-table").css("width",data.title);
			editor.setContent($html.html());
	        api.close();
	      }
	    });
	};

	// 注册一个工具栏按钮名称
	editor.ui.registry.addButton('wycustom2', {
		text: '表格宽度',
		onAction: function() {
			doSoming();
		}
	});

	// 注册一个菜单项名称 menu/menubar
	editor.ui.registry.addMenuItem('wycustom2', {
		text: '红色报表',
		onAction: function() {
			doSoming();
		}
	});

//	return {
//		getMetadata: function() {
//			return {
//				//插件名和链接会显示在“帮助”→“插件”→“已安装的插件”中
//				name: "wycustom2 plugin", //插件名称
//				url: "http://wycustom2plugindocsurl.com", //作者网址
//			};
//		}
//	};
});