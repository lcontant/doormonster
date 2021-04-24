<?php
	include "../includes/xml.inc";
	$baseURL = "http://".$_SERVER['SERVER_NAME'].":".$_SERVER['SERVER_PORT'].dirname($_SERVER['PHP_SELF'])
	."/"
	;
	function currentTime() {
		date_default_timezone_set("UTC");
		return date('Y-m-d H:i:s');
	}
	function getIP() {
		$ipAddress;
		if(isset($_SERVER['HTTP_X_FORWARDED_FOR']) && $_SERVER['HTTP_X_FORWARTDED_FOR'] != '') {
		   $ipAddress = $_SERVER['HTTP_X_FORWARDED_FOR'];
		} else {
		   $ipAddress = $_SERVER['REMOTE_ADDR'];
		}

	}
	
	
	// CONNECT TO SERVER //
	$dbh = new PDO("mysql:host=$publicHost;dbname=$dbname",$publicUser,$publicPass);
    $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	
	
	// RETRIEVE ONE CELL OF DATA //
	function retrieveData($data, $table, $id, $idValue) {
		global $dbh;
		foreach($dbh->query('SELECT '.$data.' FROM '.$table.' WHERE '.$id.'='.$idValue) as $row) {
			return $row[$data];
		}
	}
	function retrieveDataLike($data, $table, $id, $idValue) {
		global $dbh;
		foreach($dbh->query('SELECT '.$data.' FROM '.$table.' WHERE '.$id.' LIKE '.$idValue) as $row) {
			return $row[$data];
		}
	}

	
	// HOME //
	function listLatestVideos() {
		global $dbh;
		global $featuredVideo;
		foreach($dbh->query('SELECT videoID FROM video WHERE videoPublishDate <= "'.currentTime().'" ORDER BY videoPublishDate DESC LIMIT 6') as $row) {
			$thisVideo = implode(array_unique($row));
			addVideo($row, "videoLinkCompact");
		}
	}
	
	
	// VIDEOS //
	function shortenTitle($title) {
		$maxLength = 22;
		if (strlen($title) > $maxLength) {
			return substr($title,0,($maxLength-3))."...";
		} else {
			return $title;
		}
	}
	function getViews($videoID) {
		$viewCount = retrieveData('views','video','videoID','"'.$videoID.'"');
		return $viewCount;
	}
	function addVideo($selectedID, $listClass) {
		$videoID = implode(array_unique($selectedID));
		$videoTitle = retrieveData('videoTitle','video','videoID','"'.$videoID.'"');
		$videoThumbnail = retrieveData('videoThumbnail','video','videoID','"'.$videoID.'"');
		$videoPublishDate = strtotime(retrieveData('videoPublishDate','video','videoID','"'.$videoID.'"'));
		$videoYear = date("Y", $videoPublishDate);
		$views = number_format( getViews($videoID) );
		echo "<li class='".$videoID." ".$listClass."'><a href='video/".$videoID."' title='".$videoTitle."'><img src='images/videos/".$videoThumbnail."' alt='".$videoTitle."' border='0' /><br />
		<span class='videoInfo ".$videoID."'>".shortenTitle($videoTitle)."
		<br />
		<i>".$views." Views</i>
		</span></a></li>\n";
	}
	function listVideos($seriesTitle) {
		global $dbh;
		global $featuredVideo;
		foreach($dbh->query('SELECT videoID FROM video WHERE videoPublishDate <= "'.currentTime().'" AND videoSeries="'.$seriesTitle.'" ORDER BY videoPublishDate DESC') as $row) {
			$thisVideo = implode(array_unique($row));
			addVideo($row, "videoLinkCompact");
		}
	}
	function getAndPrintCommentFeed($videoID) {
		$url="http://gdata.youtube.com/feeds/api/videos/{$videoID}/comments";
		$comments=simplexml_load_file($url);
		// parse the feed using a loop
		foreach($comments->entry as $comment) {
			echo '<li>'.$comment->content.'</li>';
		}
	}
	function updateViews($id) {
		$currentViews = getViews($id);
		$updatedViews = intval($currentViews) + 1;
		global $publicHost;
		global $publicUser;
		global $publicPass;
		global $dbname;
		$link = mysqli_connect($publicHost, $publicUser, $publicPass, $dbname);
		$sql = "UPDATE video 
				SET views = '$updatedViews' 
				WHERE videoID = '$id'";
				
		if(mysqli_query($link, $sql)){
			return $updatedViews;
		} else {
			return $currentViews;
		}
	}
	
	
	// PODCASTS //
	function retrievePodcastInfo($podcastID) {
		global $dbh;
		$podcastTitle = retrieveData('podcastTitle','podcast','podcastID',$podcastID);
		$podcastSummary = retrieveData('podcastSummary','podcast','podcastID',$podcastID);
		$episodeCount = $dbh->query('SELECT COUNT(*) FROM podcastepisode WHERE podcast="'.$podcastTitle.'"')->fetchColumn();
		echo "<h2>".$podcastTitle." - ".$episodeCount." Episodes</h2>\n\t\t\t<p>".$podcastSummary."</p>\n";
	}
	
	
	// PODCAST EPISODES //
	function getLatestPublishDate($podcast) {
		global $dbh;
		foreach($dbh->query('SELECT episodePublishDate FROM podcastepisode WHERE podcast="'.$podcast.'" ORDER BY episodePublishDate DESC LIMIT 1') as $row) {
			$episodePublishDate = implode(",",$row);
			echo $episodePublishDate;
		}
	}
	function retrievePodcastEpisodes($podcast) {
		global $dbh;
		foreach($dbh->query('SELECT episodeID FROM podcastepisode WHERE podcast="'.$podcast.'" ORDER BY episodePublishDate DESC') as $row) {
			$episodeID = implode(",",$row);
			
			$episodeLink = retrieveData('episodeLink','podcastepisode','episodeID','"'.$episodeID.'"');
			$episodeThumbnail = retrieveData('episodeThumbnail','podcastepisode','episodeID','"'.$episodeID.'"');
			$episodeNum = retrieveData('episodeNum','podcastepisode','episodeID','"'.$episodeID.'"');
			$episodeName = retrieveData('episodeName','podcastepisode','episodeID','"'.$episodeID.'"');
				$getDate = strtotime(retrieveData('episodePublishDate','podcastepisode','episodeID','"'.$episodeID.'"'));
			$episodePublishDate = date("F j, Y",$getDate);
			$episodeDescription = retrieveData('episodeDescription','podcastepisode','episodeID','"'.$episodeID.'"');
		
			echo "<a href='".$episodeLink."' class='podcastEpisode'>
				<img src='images/podcasts/".$episodeThumbnail."' alt='Episode ".$episodeNum."' width='350px' height='230px' border='0' />
				<span class='podcastEpisodeInfo'>
					<h2>Episode ".$episodeNum.": ".$episodeName."</h2>
					<i>".$episodePublishDate."</i>
					".$episodeDescription."
				</span>
			</a><!--podcastEpisode-->";
		}
	}
	function retrieveRSSListing($podcast) {
		global $dbh;
		foreach($dbh->query('SELECT episodeID FROM podcastepisode WHERE podcast="'.$podcast.'" ORDER BY episodePublishDate DESC') as $row) {
			$episodeID = implode(",",$row);
			
			$episodeLink = retrieveData('episodeLink','podcastepisode','episodeID','"'.$episodeID.'"');
			$episodeNum = retrieveData('episodeNum','podcastepisode','episodeID','"'.$episodeID.'"');
			$episodeName = retrieveData('episodeName','podcastepisode','episodeID','"'.$episodeID.'"');
				$getDate = strtotime(retrieveData('episodePublishDate','podcastepisode','episodeID','"'.$episodeID.'"'));
			$episodePublishDate = date("F j, Y",$getDate);
			$episodeDescription = retrieveData('episodeDescription','podcastepisode','episodeID','"'.$episodeID.'"');
			$episodeLength = retrieveData('episodeLength','podcastepisode','episodeID','"'.$episodeID.'"');
			
			echo "<item>
					<title>".$episodeName."</title>
					<description>".$episodeDescription."</description>
					<itunes:summary>".$episodeDescription."</itunes:summary>
					<itunesu:category itunesu:code='1303' />
					<enclosure url='".$episodeLink."' type='audio/mpeg' length='1' />
					<guid>".$episodeNum."</guid>
					<itunes:duration>".$episodeLength."</itunes:duration>
					<pubDate>".$episodePublishDate."</pubDate>
				</item>";
		}
	}
	
	// FEED //
	
	function getFilePathSanitizedString($string) {
        return strtr(
            $string,
            array(
                "&amp;" => "&",
                "rsquo;" => "'",
                "#8216;" => "'",
                "&#39;" => "'"
            )
        );
    }
	function getXMLSanitizedString($string) {
			return "<![CDATA[".getFilePathSanitizedString($string)."]]>";
		}
?>