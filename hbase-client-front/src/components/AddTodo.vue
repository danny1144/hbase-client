<template>
  <div class="searchBox">
    <h2>查询HBASE</h2>
    <form @submit="submit">
      <input type="text" v-model="query.rowKeyPrefix" placeholder="请输入行键前缀值" />
      <input type="text" v-model="query.columnFamily" placeholder="请输入列簇" />
      <input type="text" v-model="query.column" placeholder="请输入列名" />
      <input type="submit"   value="搜索" />
    </form>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
export default {
  name: 'AddTodo',
  computed: {
    ...mapGetters(['getTableNames', 'query', 'selectFlag', 'getTableList']),
  },
  methods: {
    ...mapActions(['getAllTableList']),
    submit(e) {
      e.preventDefault();
      if(!this.query.tableName){
         this.$message.warning('请选择table!');
         return
      }
      this.getAllTableList(this.query);
    },
  },
};
</script>

<style scoped>
form {
  display: flex;
}
input[type='text'] {
  flex: 10;
  border: 1px solid #41b883;
  padding: 10px;
  margin-right: 10px;
}
input[type='submit'] {
  background: #41b883;
  border: 1px solid #41b883;
  color: white;
  flex: 2;
  cursor: pointer;
}
.searchBox{
  margin-bottom: 15px;
}
</style>
