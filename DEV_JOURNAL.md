# 10/31 Separate the Battle State From the View

Took a couple week break. The next few features to work on are:

 * Wrapping the current scenes in a main menu and title screen
 * Add some sort of framework for having multiple levels
 * Add some basic save / load functionality
 * Finish the basic game logic to allow victory/loss
 
The first step to making some of this a bit easier is to seperate out the state describing a battle.
Right now it is mixed in with the GUI logic of the view.

I did a refactor to pull out the state, and map data into their own classes.

Decided to go with one of the last small features before tackling the big ones, showing info on
terrain and units.

Decided to have a persistent window showing selected and hovered tile info.

# 10/14 Adding minimal AI

Using the refactored pathfinding, working on adding a minimal AI to get the example level playable.

# 10/8 Redoing distance calc functions

Needed to make the pathfinding more flexible ot allow AI to access movement.

Introduced a bug that makes enemies transparent after first unit is selected. Might have been present
before after attacking.

Turned out it was due to modifying the alpha value of a constant in the Color namespace.

# 10/7 Working on AI

Started doing groundwork to make enemy AI. Mostly refactoring to give AI access to needed info.

# 10/6 Working on BattleView

Slowing grinding through TODO list. Have been trying to follow structured commit style laid out in
https://www.conventionalcommits.org/en/v1.0.0/ .

# 10/5 Working on BattleView

As I go back to filling in the battle view logic I gave some thought to what the alpha would look
like. To add stakes to the campaign, I think the best game design is to give a starting army and
some amount of resources gain each turn based on the territory captured. The player will have to
decide the allocation of troops for each battle, knowing they won;t be immediately available for
other battles. They might decide not to commit troops to a battle that is too costly to win.

As I started to implement the actual battle logic, I realized that the enemy AI couldn't really be
on a unit by unit basis. I move the behavior to be for the whole scenario instead.

I hit a really weird bug where the mouseMoved callback in BattleView stops getting called if I
clicked and quickly moved the cursor. After a lot of fruitless debugging, it seems like this might 
be a result of the trackpad I'm using triggering a drag action or something.

# 10/4 Start Adding Missing Features to BattleView

With the refactor/cleanup/commenting totally done, it's back to adding actual features.
Created TODOs with the capabilities to fill in.

# 10/4 Refactor Package Organization

To make the organization a little clearer I organized the code that was somewhat generic to creating
a tactics game into the package `com.axlan.gdxtactics` while the code that is for the specific game
is under `com.axlan.fogofwar`. This is going to be a pretty fluid, but should help me think about
making as much of the interfaces as generic as possible.

One interesting point I hit was refactoring the ImmutableListTypeAdapterFactory. It should be
possible to infer the desired type from the generic parameter. However, since the generic is only
figured out at run time you can't access it's class information. There's probably a better way to
work around this, but I ended up having to pass it in as a function parameter.

# 10/2 Refactor TiledScreen

Refactored the tile screen to be a bit easier to understand and to properly resize.

# 9/28 - 10/1 Commenting code

Mostly finished the actual restructuring. Going back through the code to comment it, fix warnings, and other cleanup.

Learned you could classify TODOs in InelliJ so I added priority levels as I'm going through.

Some fairly major restructuring of the path finding API. Allowed the nodes to be generated on the
fly based on the tile map.

# 9/27 Massive Cleanup

Working on restructuring the code to make it cleaner. There are 3 main changes so far.

First I wanted to consolidate the constants / settings / shared resources. I created new classes to centralize this data.

Second I wanted to replace the GridPoint2 with a custom type that was immutable and fit better into the logic I was using.

Third I wanted to make the global data read into the program immutable, This turned out to be a fairly complex exercise.
GSON loads values directly into objects without setters or constructors. This meant it would skip immutable wrappers around the Lists.
To get it to actually create immutable lists, I had to create a TypeAdapterFactory.

# 9/26 Add Path Visualization

Building on the AStar demo, I worked on adding visual overlays to show the path, then animate the unit movement.

While there was some fiddling to get the positioning right, the first problem I hit was flipping the animated sprite.
The sheet only has a left facing version, so I was using the flip transform to make it face the other direction.
However, this transform was having no effect. I realized the setting the region to the frame of the animation was clearing the flip transform. I just needed to cache the state, and reapply it if necessary.

# 9/25 Starting Battle View

I spent some time dawdling since the project is getting large enough that it could use another reorganisation.
The main concern is that the shared data should be more centralized and access limited instead of just passing a bunch of references.
Also the constants should be centralized as well. For now I decided to plow forward and refactor once I get to the point of adding the concept of multiple levels.

I started adding the basic guts of the battle view and I'm at the point I want to add the pathfinding. It was a little confusing to figure out how to get the properties for the tiles, but evetually got the logic I needed.

I decided to remake my AStar app as a demo.

# 9/24 Added Stats

Added a stats file with the stats for the different types of units. Did a little refactoring to move
the deployment information to the new BattleView. 

# 9/23 Intel Selection Screen

I finished up the UI for deploying available troops. I ended up adding a layer on top of the UI to make the selected unit more obvious.

Next I added a menu to turn the various intelligence sources on and off. Nothing new with any of this.

The level descriptions are pretty complicated and theirs some unresolved subtly on what I'll want to change to do them for real.

# 9/22 Starting Map Logic

First I thought about how to move troops around and realized I would need path finding. I remembered writing a Java A* algorithm back in college https://en.wikipedia.org/wiki/A*_search_algorithm .

Next I started adding the additional data and logic to create a view for deploying troops and viewing intel.

Hit two weird issues when trying to add transparent shapes. First it seems like the tilemap renderer changes the GL settings.

I was hitting this issue: https://stackoverflow.com/questions/14700577/drawing-transparent-shaperenderer-in-libgdx , but I had to change the blend mode after the tilemap renders.

The second issue I hit was the ShapeRenderer and SpriteBatch can't have their begin and end sections overlap. The sprite batch was failing when I did this.

# 9/22 Playing Around with Graphics

I spent some time getting a little particle effects demo working.

Next I looked into getting an animation https://github.com/libgdx/libgdx/wiki/2D-Animation . Once I got the animation working, I extended the sprite class to be able to apply its transforms to make manipulating an animated sprite easier.

# 9/21 Start Laying Out BattleMap for Game

Cleaned up the map. Added atlas with tank sprites and got a decent pattern for breaking out individual files.

# 9/21 Clean up Scene / Screen Switching

I'm not totally clear on the logical orginaztion of the objects like game / scene / screen. It seems
like I can clean up my views by making them extend screen. I need to figure out how to control between them.

Basically followed the example https://libgdx.info/basic_screen/ , but used callbacks to allow the core to activate new screens.

# 9/21 Starting Map Screen

Started by trying to find other projects I could use code from.

Best I found was https://github.com/yairm210/UnCiv which is in Kotlin and https://github.com/pixdad/Tactical-Battle-System which is basically just a Tiled demo.

From there I started looking at https://github.com/libgdx/libgdx/wiki/Tile-maps which got me to download Tiled map editor.

Since neither of the examples I found seemed sufficient, I decided to look for a sprite pack. https://itch.io/game-assets/free/tag-tilemap seemed like the best set of resources.

https://www.spriters-resource.com/ is also good for published game sheets.

The first decision is what sort of geometry to use, orthogonal, isometric, or hex. I decided to go with the simplest Ortho projection for now. On top of that the Advanced Wars spritesheet seems like a good mock up for now.

I was able to get a decent initial implementation by taking https://github.com/pixdad/Tactical-Battle-System and cleaning up the interfaces a bit. 

Next I started using my own advanced wars map. This led me to play with the scaling on work on the tile to pixel transforms.

Turns out https://github.com/pixdad/Tactical-Battle-System was just a copy of https://www.gamefromscratch.com/post/2014/05/01/LibGDX-Tutorial-11-Tiled-Maps-Part-2-Adding-a-character-sprite.aspx

# 9/20 Implementing Menus

I created a class to layout and control the briefing dialogue. Following the table guide, I was mostly able to reproduce the layout without issue.

The one strangeness I hit was when I wanted a button to be on top of a label. Initially I tried a stack, but the button would will the space allocated to the stack covering up the label. Nothing I did seem to affect this. Next I tried a WidgetGroup. I still don't really understand how these work, but it got the layout I wanted by default.

Since I wouldn't actually want the text to overlap, I ended up going back to just using a table, but still shows I don't fully understand the advanced layout behaviors.

Churned through the first couple menus without much more incident.

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