// JavaScript Document

$(document).ready(function() {

// VIDEOS

		if ($('.playlist')[0]){
				var videoID = $('.featuredVideo').attr('class').split(' ')[1];
				if ($( ".videoList li" ).hasClass( videoID )){
						$("li." + videoID + " a").toggleClass("selected")
					}
				var index = $( "li." + videoID ).index();
				var videoListLength = $(".videoList li").length;
				var totalPages = Math.floor(videoListLength / 6);
				var startPage = Math.floor(index / 6);
				var listPosition = startPage * -912;
				var currentPage = startPage;
				$('.videoList').css('left', listPosition + 'px');
				function updatePlaylistButtons() {
						if ( videoListLength <= 6 ) {
							$('.leftButton').addClass('deadButton')
							$('.prevVideo').css("pointer-events", "none");
							$('.rightButton').addClass('deadButton')
							$('.nextVideo').css("pointer-events", "none");
						} else {
							if ( currentPage <= 0 ) {
								$('.leftButton').addClass('deadButton')
								$('.prevVideo').css("pointer-events", "none");
								$('.rightButton.deadButton').removeClass('deadButton')
								$('.nextVideo').css("pointer-events", "auto")
							} else if ( currentPage >= totalPages ) {
								$('.rightButton').addClass('deadButton')
								$('.nextVideo').css("pointer-events", "none");
								$('.leftButton.deadButton').removeClass('deadButton')
								$('.prevVideo').css("pointer-events", "auto")
							} else {
								$('.leftButton.deadButton').removeClass('deadButton')
								$('.prevVideo').css("pointer-events", "auto")
								$('.rightButton.deadButton').removeClass('deadButton')
								$('.nextVideo').css("pointer-events", "auto")
							}
						}
					}
				updatePlaylistButtons();
				$('.nextVideo').on("click",function() {
					if ( currentPage !== totalPages ) {
						$('.videoList').finish().animate({'left' : '-=912px'}, 500, function() {

							});
						currentPage = currentPage + 1;
						updatePlaylistButtons();
					}
				});
				$('.prevVideo').on("click",function() {
					if ( currentPage !== 0 ) {
						$('.videoList').finish().animate({'left' : '+=912px'}, 500, function() {

							});
						currentPage = currentPage - 1;
						updatePlaylistButtons();
					}
				});
		}

// SERIES
		var minHeight = parseInt(($('.videoListVertical').css('max-height')));
		if (minHeight){
		  minHeight.replace(/[^0-9\.]/g, '');
    }
		if ($('.videos-banner').length > 0) {
			$('.videoBlock').each(function() {
				var totalVids = $(this).find('li').length;
				if ( totalVids > 6 ) {
					$(this).find('.expand').css({'display' : 'block'});
				}
			});
			$('.expand').on("click",function() {
				var totalVids = $(this).closest('.videoBlock').find('li').length;
				var rows = Math.floor(totalVids / 3 + 1);
					var videoHeight = parseInt(($(this).closest('.videoBlock').find('li').css('height')).replace(/[^0-9\.]/g, ''));
					//var videoHeight = 230;
					var videoBorderHeight = parseInt((($(this).closest('.videoBlock').find('li').css('border-width')).replace(/[^0-9\.]/g, ''))*2);
					//var videoMargin = parseInt((($(this).closest('.videoBlock').find('li').css('margin')).replace(/[^0-9\.]/g, ''))*2);
					var videoMargin = 30;
					var videoBlockPadding = parseInt((($(this).closest('.videoBlock').css('padding')).replace(/[^0-9\.]/g, ''))*2);
					var videoBlockTitle = parseInt((($(this).closest('.videoBlock').find('h2').css('margin-top')).replace(/[^0-9\.]/g, ''))*2);
					var videoExpandBtnMargin = parseInt((($(this).css('margin-top')).replace(/[^0-9\.]/g, '')));
					var margins = 60;
					var videoBlockDefaultHeight = parseInt(($(this).closest('.videoBlock').find('.videoListVertical').css('max-height')).replace(/[^0-9\.]/g, ''));
				//var videoHeight = videoHeight + videoBorderHeight + videoMargin;
				//var maxHeight = videoHeight * rows + videoBlockPadding + videoExpandBtnMargin + videoBlockTitle;
				var maxHeight = ( videoHeight + videoMargin ) * rows + margins;
				alert ( '(' + videoHeight + '+' + videoMargin + ')' + '*' + rows + '+' + margins + '=' + maxHeight);
				var transitionTime = rows * 100;
				if ( $(this).hasClass('closed') ) {
					$(this).closest('.videoBlock').children('.videoListVertical').animate({'max-height' : maxHeight + 'px'}, transitionTime);
					$(this).removeClass('closed').addClass('open');
				} else {
					$(this).closest('.videoBlock').children('.videoListVertical').animate({'max-height' : minHeight + 'px'}, transitionTime);
					$(this).removeClass('open').addClass('closed');
				}
			});
		}

// PODCASTS

		function animatePointer(position) {
				$('.pointer').animate({'left': position});
			}

		function changePodcast(podcastID) {
				$('.podcastActive').animate({'opacity' : '0'}, function() {
						$('.podcastActive').removeClass('podcastActive').addClass('podcastInactive');
						$('.podcast' + podcastID).removeClass('podcastInactive').addClass('podcastActive');
						$('.podcastActive').animate({'opacity' : '100'});
					});
				$('.podcastListActive').animate({'opacity' : '0'}, function() {
						$('.podcastListActive').removeClass('podcastListActive').addClass('podcastListInactive');
						$('.podcastList' + podcastID).removeClass('podcastListInactive').addClass('podcastListActive');
						$('.podcastListActive').animate({'opacity' : '100'});
					});
			}

		$('.podcastArtSHCF').on("click",function() {
				animatePointer('10%');
				changePodcast('5');

			});
		$('.podcastArtLive').on("click",function() {
				animatePointer('37%');
				changePodcast('4');

			});
		$('.podcastArtRoadtrip').on("click",function() {
				animatePointer('63%');
				changePodcast('1');
			});
		$('.podcastArtWhiteNoise').on("click",function() {
				animatePointer('90%');
				changePodcast('2');
			});

// SEARCH

		$(".searchResultItem").sort(function(a,b){
			return new Date($(a).attr("data-date")) > new Date($(b).attr("data-date"));
		}).each(function(){
			$("#searchResults").prepend(this);
		})

	});
