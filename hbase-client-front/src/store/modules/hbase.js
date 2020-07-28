import axios from 'axios';
import config from '../../config/index';
const state = {
  tableNames: [],
  selectFlag: false,
  limit: 20,
  tableList: [],
  tableTitles: [],
  query: {
    tableName: '',
    currentPage: 1,
    pageSize: 10,
    rowKeyPrefix: '',
    startRowKey: '',
    endRowKey: '',
    columnFamily: '',
    column: '',
  },
};
const className = 'v1';
const getters = {
  selectFlag: state => state.selectFlag,
  query: state => state.query,
  getTableNames(state) {
    return state.tableNames;
  },
  getTableList(state) {
    return state.tableList;
  },
};
const mutations = {
  setSelectFlag: (state, flag) => (state.selectFlag = flag),
  setQuery: (state, query) => (state.query = query),
  setCurrentTable: (state, tableName) => (state.query.tableName = tableName),
  setTableNames(state, tableNames) {
    state.tableNames = tableNames;
  },
  setTableList(state, tableList) {
    state.tableList = tableList;
  },
};
const request = axios.create({
  baseURL: `${config.serverUrl}hbase-client`,
});

const actions = {
  async getAllTableNames({ commit }) {
    const res = await request.get(`/${className}/allTables`);
    commit('setTableNames', res.data.data);
  },
  async getAllTableList({ commit }, query) {
    const res = await request.post(`/${className}/pageQuery`, query);
    commit('setTableList', res.data.data);
  },
  async setCurrentTable({ dispatch, commit }, tableName) {
    commit('setCurrentTable', tableName);
    dispatch('getAllTableList', state.query);
  },
};
export default { state, getters, mutations, actions };
