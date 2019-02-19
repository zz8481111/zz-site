# My http://zz8481111.github.io site generator
## WIP

repository https://github.com/zz8481111/zz8481111.github.io is git-submodule 'public'

about [shadow-cljs](https://github.com/thheller/shadow-cljs)

## Start http://localhost:8080 server
    
    $ npm install
    $ shadow-cljs watch frontend

>    or 

    $ npx shadow-cljs watch frontend

>    or via script

    $ npm run dev

## Generate js and send to github-pages repository:

    $ npm run clean
    $ npm run release
    $ npm run push-public

## License

MIT
