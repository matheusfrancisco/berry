## Berry
is an opinated blog build in cljs(helix), clj, babashka

Soon this will be a custom blog engine


## Req:
- clojure, clojurescript
- babashka

## Posts
you should write your posts inside /posts/ using markdown format


## TODO
in this repo may have some setup how to use tsx in he clojurescript building the components, how
to invoke clj or cljs using cljc. Everything will be used soon to make those todo happen

- [ ] make an engine
- [ ] make the post engine more easy to use
- [ ] deploy to clojars as .jar
- [ ] dpeloy to npm to using with npm

## Watch Dev/Tests

Build the developer build and start shadow-cljs watching and serving main 
in localhost:8000 and tests in localhost:8100

```sh
npm i
bb build-posts
npm run watch
```

## Release
Build the release package to production deploy

```sh
bb build-posts
npm run release
```

## License
Copyright © 2023 matheusfrancisco

This is free and unencumbered software released into the public domain. 
For more information, please refer to http://unlicense.org
