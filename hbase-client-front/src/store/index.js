import hbase from './modules/hbase';
import Vue from 'vue';
import Vuex from 'vuex';
Vue.use(Vuex);
const store = new Vuex.Store({
  modules: {
    hbase,
  },
});

export default store;
