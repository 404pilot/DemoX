## Mac

### Homebrew

App installation location: `/usr/local/Cellar/`

It will create corresponding link under `/usr/local/bin/`

Thus, set `/etc/paths` to

	/usr/local/bin
	/usr/bin
	/bin
	/usr/sbin
	/sbin

Put `/usr/local/bin` at the first line.

Or go to shell configuration and add `/usr/local/bin` to `$PATH`.

Restart terminal and test it

	$ which git
	/usr/local/bin/git

In this way, I may not need to explicitly specify command home location for `JAVA_HOME` or `MAVEN_HOME` in `.bashrc`. It will automatically use the default one, i,e, the first line in `/etc/paths`.

Use `brew info zsh` to see those dependecies 

#### brew-cask

	# install brew-cask
	
	brew tap phinze/homebrew-cask
	brew install brew-cask

	# update brew-cask list
	
	brew update
	brew upgrade brew-cask
	
	rm -rf /usr/local/Cellar/brew-cask/_old_version

What's more, brew-cask installation location is `/opt/homebrew-cask/Caskroom/`

More information, just go to github page.

###### java location

brew install jdk @->`/Library/Java/JavaVirtualMachines/`

system @-> `/System/Library/Frameworks/JavaVM.framework/Versions/`

`/usr/bin/java` @-> `/System/Library/Frameworks/JavaVM.framework/Versions/Current/Commands/java`

`mac/System Preferences/java` @-> `/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/bin/java`

### Open app in terminal
	alias sublime='open -a "Sublime Text 2"'
	alias mou='open -a Mou'


### System Applications Location

	/System/Library/CoreServices/Finder.app
	/System/Library/CoreServices/Spotlight.app

Find the location by opening `Activity Monitor` to see the information for the process.

### External Monitor Fonts

	defaults -currentHost read -globalDomain AppleFontSmoothing
	
>2014-02-22 15:07:37.398 defaults[15657:507]
The domain/default pair of (kCFPreferencesAnyApplication, AppleFontSmoothing) does not exist
	
	defaults -currentHost write -globalDomain AppleFontSmoothing -int 2
	defaults -currentHost delete -globalDomain AppleFontSmoothing


### No Sound?
	sudo kextunload /System/Library/Extensions/AppleHDA.kext
	sudo kextload /System/Library/Extensions/AppleHDA.kext

### Screenshot

	shift + command + 4 then press space
	
### Wanna lock screen?

	shift + control + power
	
### Wanna see file details (whole file name) 
	space on file

### Cut and Paste inside Finder

	command + c
	command + option + v
	
### Apply default settings to Finder
	command + j
	then hold option to ** restore **
	
### Shortcuts
	# forward delete
	Fn + delete
