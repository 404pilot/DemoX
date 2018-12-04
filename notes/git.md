## GIT

##### 
```
# checkout remote branch in a local branch with same name
git checkout -b test <name of remote>/test
```

```
# yesterday
$ git commit 'WIP'
$ git push

# continue work today
$ git reset HEAD^
$ git push -f
```

##### discard Changes
    git checkout -- .
    git checkout _commit/tag                    // 'detached HEAD' state
    git checkout _commit/tag -b newBranch
    git reset --hard HEAD~1
    git reset --hard _commit
    git reset
    git reset --soft HEAD^
    git revert _shortHashvalue

> working | staging | local commit(x) | repo

- `reset --hard`:commit(y) | commit(y) | commit(y) 
- `reset`:working | clear staging | commit
- `reset --soft HEAD^`:working | staging | HEAD~1
- `checkout -- .`: index | staging | commit
- `checkout commitId`: [detached mode] then create a new branch `checkout -b` based on this commit
- `revert`: remotely rollback

##### show unmerged branches
    git branch -a --no-merged master
    git branch -a --no-merged master | grep B-

##### stash
    git stash || git stash save '_name'
    git pop || git stash apply || git stash apply stash@{1}
    git stash drop || git stash drop stash@{1} || git stash clear
    git stash list

##### index deleted files
	git add -u

##### delete remote branch
	git branch -d cyclomatic_test
	git push origin :cyclomatic_test

##### re-apply .gitignore
	git rm -r --cached .
	
##### commit amend and force push
	git add .
	git commit --amend
	git push -f	// git push -f origin _branch_name
	
##### merge
	git merge --no-ff develop
	
##### sync
	git fetch --prune
	
	git remote prune origin
	
	# prune one branch
	git branch -dr _branchname

##### init
	git remote add origin git@github.com:lovelypeter/first-proj.git
	git push -u origin master
or modify `.git/config`

##### tag
	git tag -a 1.14.0-RELEASE -m '1.14.0-RELEASE'
	git push origin 1.14.0-RELEASE
	# re-tag
	git tag -a 1.14.0-RELEASE -m '1.14.0-RELEASE' -f
	
	# once after re-tagging, force to sync all tags
	git fetch --tags
	git fetch
- `fetch --tags` force to sync all tag
- `fetch` sync tags & branch heads

##### find unmerged or merged branch
	git branch -a --no-merged master | grep B-
	git branch -a --merge master | grep B-
	# directly check remote repository
	git branch --remote --no-merged master
	
##### merge two commits

> git log

> b

> a

	git rebase --interactive HEAD~2

change
> pick b76d157 a

> pick a931ac7 b

to
> pick b76d157 a

> `squash` a931ac7 b

##### other
	git config --list


##### git config

global `~/.gitconfig`

local `_localRepo/.git/config`
	
	git config --global user.name 'xxx'
	git config --global user.email 'xxx'
	
	git config --global user.name 'neatpilot'
	git config --global user.email "neatpilot@users.noreply.github.com"
	
	git config --global user.email

	[user]
		name = **
		email = **
	[color]
		ui = true
	[push]
		default = current
	[alias]
		co = checkout
		st = status
		br = branch
		lol = log --oneline -8
		byebye = reset --hard HEAD
		timetravel = reset --hard HEAD~1
		cmaster = "checkout master"
		crelease = "checkout #release"

##### git fork

	git clone https://github.com/_name/_project-name.git
	
	// add origin repo branch to track from
	git remote add orepo https://github.com/_origin/_project-name.git
	
	// check repositories
	git remote -v
	
	// sync latest origin repo's master
	git checkout master
	git pull orepo master
	
	// convenient way to see all branches
	git branch -va
	
	// merge specifc branch from orepo (origin repo)
	git merge orepo/_specifc_branch
	
	// delete br after orepo deleting it
	git push origin :_deletedBranch

##### Reminders
If you are going to revise history, there will be a problem if somebody fork your project. History will be a mess.

	git commit --amend & git push origin:_branch_name 

	git reset --hard HEAD~1 & git push origin:_branch_name 
