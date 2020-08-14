<template>
  <div>
    <a-range-picker
      :show-time="{ format: 'HH:mm' }"
      format="YYYY-MM-DD HH:mm"
      :placeholder="['Start Time', 'End Time']"
      @change="onChange"
      @ok="onOk"
    />
  </div>
</template>
<script>
import { mapMutations } from 'vuex';

export default {
  methods: {
    ...mapMutations(['setDate']),
    onChange(value, dateString) {
      console.log('Selected Time: ', value.length);
      console.log('Formatted Selected Time: ', dateString);
      if (value.length === 0) {
        this.setDate({ startTime: '', endTime: '' });
        return;
      }
      this.setDate({ startTime: new Date(value[0]['_d']).getTime(), endTime: new Date(value[1]['_d']).getTime() });
    },
    onOk(value) {
      console.log('onOk: ', new Date(value[0]['_d']).getTime());
      this.setDate({ startTime: new Date(value[0]['_d']).getTime(), endTime: new Date(value[1]['_d']).getTime() });
    },
  },
};
</script>
