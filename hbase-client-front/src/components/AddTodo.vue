<template>
  <div class="searchBox">
    <h2>查询HBASE</h2>
    <form @submit="submit">
      <date-picker class="picker"></date-picker>
      <input type="submit"   value="搜索" />
    </form>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import DatePicker from './DatePicker'
export default {
  name: 'AddTodo',
  components: {
    DatePicker
  },
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
      console.log(this.$store.state.hbase.query)
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
  flex: 1;
  cursor: pointer;
}
.picker{
   flex: 3;
}
.searchBox{
  margin-bottom: 15px;
}
</style>
