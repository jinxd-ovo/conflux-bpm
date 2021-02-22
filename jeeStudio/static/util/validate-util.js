$.validator.setDefaults({
	errorPlacement: function(error, element) {
		if($(element).closest("fieldset").length > 0){
			if ($(element).closest("fieldset").next("div").hasClass("tooltip")) {
				$(element).closest("fieldset").attr({
					"data-original-title": $(error).text()
				}).tooltip('show');
			} else {
				$(element).closest("fieldset").attr({
					"data-original-title": $(error).text(),
					"data-placement": "bottom",
					"data-trigger":"manual",
					"data-selector":"blockquote"
				}).tooltip('show');
				$(element).closest("fieldset").next("div.tooltip").css("margin-top","-10px");
			}
		}else{
			if ($(element).next("div").hasClass("tooltip")) {
				$(element).attr({
					"data-original-title": $(error).text()
				}).tooltip('show');
				$(element).next("div.tooltip").css("margin-top","-10px").removeClass("top").addClass("bottom");
				if($(element).hasClass("select2")){
					$(element).next("div.tooltip").css({"margin-top":"50px","margin-left":"100px"}).removeClass("top").addClass("bottom");
				}
			} else {
				$(element).attr({
					"data-original-title": $(error).text(),
					"data-placement": "bottom",
					"data-trigger":"manual",
					"data-selector":"blockquote"
				}).tooltip('show');
				$(element).next("div.tooltip").css("margin-top","-10px").removeClass("top").addClass("bottom");
				if($(element).hasClass("select2")){
					$(element).next("div.tooltip").css({"margin-top":"50px","margin-left":"100px"}).removeClass("top").addClass("bottom");
				}
			}
		}
	},
 	unhighlight: function(b) {
 		if($(b).closest("fieldset").length > 0){
 			$(b).closest("fieldset").tooltip('destroy');
			$(b).removeClass("error");
 		}else{
 			$(b).tooltip('destroy');
			$(b).removeClass("error");
 		}
	}
});

var ValidateUtil = {
	init:function($layero){
		$layero.find("input[type='text'].required").closest("fieldset").after('<div class="required-label"><span>!</span></div>');
		$layero.find("textarea.required").after('<div class="required-label"><span>!</span></div>');
		$layero.find("select.required").after('<div class="required-label"><span>!</span></div>');
		$layero.find("input[type='radio'].required,input[type='checkbox'].required").closest(".i-checks-div").after('<div class="required-label"><span>!</span></div>');
		
		$layero.on("select2:select","select.required",function(){
			if($.trim($(this).val()).length > 0){
				$(this).closest("fieldset").siblings("div.tooltip").hide();
			}
		})
		$layero.on("ifClicked","input[type='checkbox'].required",function(){
			if($.trim($(this).val()).length > 0){
				$(this).closest("fieldset").siblings("div.tooltip").hide();
			}
		})
	},
	closeTooltip:function($obj){
		if($.trim($obj.val()).length > 0){
			$obj.closest("fieldset").siblings("div.tooltip").hide();
		}
	}
}


