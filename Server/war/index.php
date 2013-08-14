<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    <title>AppletLoader</title>
  </head>
  <body>
  
 <?php 

     $app_id = "250688458313215";

     $canvas_page = "http://armageddonwaffle.appspot.com/";

     $auth_url = "http://www.facebook.com/dialog/oauth?client_id=" 
            . $app_id . "&redirect_uri=" . urlencode($canvas_page);

     $signed_request = $_REQUEST["signed_request"];

     list($encoded_sig, $payload) = explode('.', $signed_request, 2); 

     $data = json_decode(base64_decode(strtr($payload, '-_', '+/')), true);
	
	$appletParamUserID;
	//$appletParamName;
		
     if (empty($data["user_id"])) {
            echo("<script> top.location.href='" . $auth_url . "'</script>");
			$appletParamUserID = "";
			//$appletParamName = "";
     } else {
            echo ("Welcome User: " . $data["user_id"]);
			//echo ("Welcome User: " . $data["user"]);
			$appletParamUserID = $data["user_id"];
			//$appletParamName = $data["user"];
     } 
	
 ?>

  <applet code="org.lwjgl.util.applet.AppletLoader" archive="lwjgl_util_applet.jar, lzma.jar" codebase="." width="680" height="510">
  
    <!-- The following tags are mandatory -->
    
    <!-- Name of Applet, will be used as name of directory it is saved in, and will uniquely identify it in cache -->
    <param name="al_title" value="appletloadertest">
    
    <!-- Main Applet Class -->
    <param name="al_main" value="org.newdawn.slick.AppletGameContainer">
    
    <param name="game" value="edu.cornell.slicktest.ClientEngine">
    
    <!-- List of Jars to add to classpath -->
    <param name="al_jars" value="mygame.jar, slf4j-api-1.6.3.jar, jbox2d-library-2.1.2.1-SNAPSHOT.jar, lwjgl_applet.jar.pack.lzma, lwjgl.jar.pack.lzma, jinput.jar.pack.lzma, lwjgl_util.jar.pack.lzma, slick.jar, jogg-0.0.7.jar, jorbis-0.0.15.jar">
    
    <!-- signed windows natives jar in a jar --> 
    <param name="al_windows" value="windows_natives.jar.lzma">
    
    <!-- signed linux natives jar in a jar --> 
    <param name="al_linux" value="linux_natives.jar.lzma">
    
    <!-- signed mac osx natives jar in a jar --> 
    <param name="al_mac" value="macosx_natives.jar.lzma">

    <!-- signed solaris natives jar in a jar --> 
    <param name="al_solaris" value="solaris_natives.jar.lzma">
    
    <!-- Tags under here are optional -->
    
    <!-- whether to use cache - defaults to true -->
    <!-- <param name="al_cache" value="true"> -->
    
    <!-- Version of Applet (case insensitive String), applet files not redownloaded if same version already in cache -->
    <!-- <param name="al_version" value="0.1"> -->
    
    <!-- Specify the minimum JRE version required by your applet, defaults to "1.5" -->
    <!-- <param name="al_min_jre" value="1.6"> -->
    
    <!-- background color to paint with, defaults to white -->
    <!-- <param name="boxbgcolor" value="#000000"> -->
    
    <!-- foreground color to paint with, defaults to black -->
    <!-- <param name="boxfgcolor" value="#ffffff"> -->
    
    <!-- logo to paint while loading, will be centered, defaults to "appletlogo.gif" -->
    <!-- <param name="al_logo" value="appletlogo.gif"> -->
    
    <!-- progressbar to paint while loading. Will be painted on top of logo, width clipped to percentage done, defaults to "appletprogress.gif" -->
    <!-- <param name="al_progressbar" value="appletprogress.gif"> -->
    
    <!-- whether to run in debug mode -->
    <param name="al_debug" value="false">
    
    <!-- whether to prepend host to cache path - defaults to true -->
    <!-- <param name="al_prepend_host" value="true"> -->
  	
    <param name="separate_jvm" value="true">
	
	<param name="userID" value="<?php echo $appletParamUserID; ?>">
  </applet>

  <p>
    if <code>al_debug</code> is true the applet will load and extract resources with a delay, to be able to see the loader process.
  </p>
   


  </body>
</html>
