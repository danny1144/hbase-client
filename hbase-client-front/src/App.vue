<template>
  <div id="app">
    <AddTodo />
    <SelectTable/>
    <Tables />
  </div>
</template>

<script>
import Tables from './components/Tables'
import AddTodo from './components/AddTodo'
import SelectTable from './components/SelectTable'
import store from './store';
export default {
  name: 'app',
  store,
  components: {
    Tables,
    AddTodo,
    SelectTable
  },
};
</script>
<style>
body {
  background: #fff;
}
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: left;
  width: 1100px;
  margin: 60px auto;
}
h3 {
  padding: 10px;
  margin: 0;
}
</style>
