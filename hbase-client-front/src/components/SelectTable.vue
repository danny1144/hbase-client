<template>
  <div class="selectBox">
    <a-select placeholder="请选择表" style="width: 300px" @change="handleChange">
      <a-icon slot="suffixIcon" type="smile" />
      <a-select-option v-for="(table) in getTableNames" v-bind:key="table">{{table}}</a-select-option>
    </a-select>
  </div>
</template>
<script>
import { mapGetters, mapActions, mapMutations } from 'vuex';

export default {
  computed: {
    ...mapGetters(['getTableNames', 'query']),
  },
  methods: {
    ...mapMutations(['setSelectFlag']),
    ...mapActions(['getAllTableNames', 'setCurrentTable']),
    handleChange(value) {
      this.query.rowKeyPrefix = '';
      this.query.columnFamily = '';
      this.query.column = '';
      this.setCurrentTable(value);
      this.setSelectFlag(true);
    },
  },
  created() {
    this.getAllTableNames();
  },
};
</script>
<style scoped>
.selectBox {
  margin-bottom: 15px;
}
</style>