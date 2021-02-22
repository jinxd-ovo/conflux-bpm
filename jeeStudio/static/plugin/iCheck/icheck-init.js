$(document).ready(function () {
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green',
    });
    
    
    
    var iCheckScript = '<script>'+
			'$(document).ready(function(){'+
				'$(".i-checks").each(function(i,dom) {'+
					'if($(this).parents("fieldset").prop("disabled")){'+
							"$(this).iCheck('disable');"+
					'}'+
				'})'+
			'})'+
		'<\/script>';
	$("body").append(iCheckScript);
});

