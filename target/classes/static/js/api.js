$(function() {
	$('#content')[0].width = $('.iframe')[0].offsetWidth
	$('.iframe')[0].offsetHeight = $('#content')[0].contentWindow.height
})
