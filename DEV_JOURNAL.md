# 9/20 Briefing View

I created a class to layout and control the briefing dialogue. Following the table guide, I was mostly able to reproduce the layout without issue.

The one strangeness I hit was when I wanted a button to be on top of a label. Initially I tried a stack, but the button would will the space allocated to the stack covering up the label. Nothing I did seem to affect this. Next I tried a WidgetGroup. I still don't really understand how these work, but it got the layout I wanted by default.

Since I wouldn't actually want the text to overlap, I ended up going back to just using a table, but still shows I don't fully understand the advanced layout behaviors.

# 9/19 Starting Actual Project

Going to start porting the code from https://github.com/axlan/Conqueror-of-Empires/tree/fog_war

First I'll take the level loading logic. I played around with the JSON to Java object parsing and found GSON https://github.com/google/gson seemed good.

With that done, I went back to https://www.gamefromscratch.com/post/2015/02/03/LibGDX-Video-Tutorial-Scene2D-UI-Widgets-Layout-and-Skins.aspx and got it working with VisUI.

From there I started working on the briefing view. It looks like I have all the tools I need to put it together, just figuring out the formatting.
https://github.com/libgdx/libgdx/wiki/Table gives the details I need to do the layout.

#9/19 Experiment with LML and starting game 

Following the recommendation of one of the tutorials I decided to take a look at https://github.com/czyzby/gdx-lml . I spent a good amount of time learning the ins and outs of the markdown language. 

After some problems with attributes I realized a list of tags and attributes is in: 

https://github.com/czyzby/gdx-lml/blob/master/lml/src/main/java/com/github/czyzby/lml/parser/impl/DefaultLmlSyntax.java 
and 
https://github.com/czyzby/gdx-lml/blob/master/lml-vis/src/main/java/com/github/czyzby/lml/vis/parser/impl/VisLmlSyntax.java 
After some poking I was able to size objects too. 

However, it looks like it's just not worth the additional obfuscation of the code.  http://czyzby.github.io/gdx-lml/lml-vis/ is still useful for looking at the available layout. 

VisUI https://github.com/kotcrab/vis-ui still looks useful enough to use as the base skin for development. See also https://vis.kotcrab.com/demo/ui/ 
 
# 9/18 Learning to do basic UI

Went down a HUGE rabbit hole. I spent basically 9 hours figuring out how to style the UI in libGDX 

Started with: 

https://www.gamefromscratch.com/post/2015/02/03/LibGDX-Video-Tutorial-Scene2D-UI-Widgets-Layout-and-Skins.aspx 

This gave a basic example of how to create UI elements (see https://github.com/libgdx/libgdx/wiki/Scene2d.ui ). There were a few problems (Align was moved to com.badlogic.gdx.utils.Align and I needed to download Hiero from here https://libgdx.badlogicgames.com/tools.html instead of running the included JAR), but I got it working without too much trouble. 
 
Next I wanted to draw a textbox with a border. After a lot of digging I realized you needed to set a ninepatch as the background of an element to create a border. I found https://github.com/libgdx/libgdx/wiki/Ninepatches which sort of explains how they work. However I wanted to be able to use a .9.png so I didn't need to specify the border manually. I found the tool  

https://romannurik.github.io/AndroidAssetStudio/nine-patches.html to generate them. I ended up making a basic rectangle border ninepatch in GIMP. 

A lot of stuff I was reading recommended using the Skin object to handle UI configuration https://github.com/libgdx/libgdx/wiki/Skin which is what parses the UI JSON. I sort of figured out how it worked, but I couldn't understand how the images were being packaged into ninepatches 

After lots and lots of digging I found: https://www.gamefromscratch.com/post/2013/12/18/LibGDX-Tutorial-9-Scene2D-Part-3-UI-Skins.aspx as an example which shows that a lot of the associations are happening automagically. Basically the JSON, atlas, and spritesheet seem to just need to have the same name. The JSON seems to automatically be aware of the atlas regions and seems to be able to fix the font image paths. Eventually I realized the sane way to do all this is to use the tool https://ray3k.wordpress.com/software/skin-composer-for-libgdx/ which can be downloaded https://github.com/raeleus/skin-composer . There's even a gallery of example UI's https://github.com/czyzby/gdx-skins which also has links to other useful articles. 