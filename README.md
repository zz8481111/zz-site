# My http://zz8481111.github.io site generator
## WIP

repository https://github.com/zz8481111/zz8481111.github.io

## Start http://localhost:8080 server
    
    $ npm install
    $ shadow-cljs watch frontend

    or 

    $ npx shadow-cljs watch frontend

    see https://github.com/thheller/shadow-cljs

## Generate js and send to github-pages repository:

    $ rm -rf public/js
    $ shadow-cljs release frontend
    $ cd public
    $ git push origin master
    $ cd ..

## License

MIT
