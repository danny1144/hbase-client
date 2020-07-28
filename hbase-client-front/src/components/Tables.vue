<template>
  <a-table
    :columns="getTableList.columns"
    :data-source="getTableList.resultList"
    :pagination="{ pageSize: getTableList.pageSize,total:getTableList.totalCount,
    showTotal:(total, range) => `${range[0]}-${range[1]} of ${total} items` ,
    }"
    @change="onChange"
  >
    <a slot="name" slot-scope="text">{{ text }}</a>
    <template slot="operation" slot-scope="text, record">
      <div class="editable-row-operations">
        <span v-if="record.editable">
          <a @click="() => save(record.key)">Save</a>
          <a-popconfirm title="Sure to cancel?" @confirm="() => cancel(record.key)">
            <a>Cancel</a>
          </a-popconfirm>
        </span>
        <span v-else>
          <a :disabled="editingKey !== ''" @click="() => edit(record.key)">Edit</a>
        </span>
      </div>
    </template>
  </a-table>
</template>
<script>
/* const columns = [
  {
    title: 'Contry',
    dataIndex: 'contry',
    key: 'name',
    // scopedSlots: { customRender: 'name' },
  },
  {
    title: 'compressorConvertEff',
    dataIndex: 'compressorConvertEff',
    key: 'compressorConvertEff',
    width: 80,
  },
  {
    title: 'City',
    dataIndex: 'city',
    key: 'address 1',
    ellipsis: true,
  },
  {
    title: 'id',
    dataIndex: 'id',
    key: 'address 2',
    ellipsis: true,
  },
  {
    title: 'operation',
    dataIndex: 'operation',
    scopedSlots: { customRender: 'operation' },
  },
]; */
import { mapGetters, mapActions, mapMutations } from 'vuex';
export default {
  data() {
    return {
      editingKey: '',
      data: this.$store.state.tableList,
    };
  },
  computed: {
    ...mapGetters(['getTableNames', 'query', 'selectFlag', 'getTableList']),
  },
  methods: {
    ...mapActions(['getAllTableList']),
    ...mapMutations(['setQuery']),

    onChange(cur) {
      //{pageSize: 10, total: 100, current: 2, showTotal: ƒ}
      this.query.currentPage = cur.current;
      this.query.pageSize = cur.pageSize;
      this.getAllTableList(this.query);
    },
  },
  created() {
    if (this.selectFlag) {
      this.getAllTableList(this.query);
    }
  },
};
</script>
