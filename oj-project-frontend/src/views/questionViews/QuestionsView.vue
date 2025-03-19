<template>
  <div id="questionsView">
    <h2>Manage Question</h2>
    <a-space direction="vertical" size="large" :style="{ width: '500px' }">
      <a-form :model="searchParameters" layout="vertical">
        <a-form-item field="title" label="Title">
          <a-input
            v-model="searchParameters.title"
            placeholder="please enter title"
          />
        </a-form-item>
        <a-form-item field="tags" label="Tags">
          <a-input-tag
            v-model="searchParameters.tags"
            placeholder="please enter tags"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="doSubmit">Submit</a-button>
        </a-form-item>
      </a-form>
    </a-space>

    <a-table
      :columns="columns"
      :data="dataList"
      :pagination="{
        pageSize: searchParameters.pageSize,
        current: searchParameters.current,
        total,
        showTotal: true,
        showPageSize: true,
      }"
      @page-change="onPageChange"
      @page-size-change="onSizeChange"
    >
      <template #tags="{ record }">
        <a-space wrap>
          <a-tag v-for="(tag, index) of record.tags" :key="index" color="green"
            >{{ tag }}
          </a-tag>
        </a-space>
      </template>
      <template #acceptanceRate="{ record }">
        {{
          `
          ${
            record.submitNum ? record.acceptedNum || 0 / record.submitNum : "0"
          }%
        (${record.acceptedNum || 0}/${record.submitNum || 0})
        `
        }}
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("MMM Do YYYY") }}
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="toQuestionPage(record)"
            >Practice This Question
          </a-button>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import { Question, QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";

//search
const doSubmit = () => {
  searchParameters.value = {
    ...searchParameters.value,
    current: 1,
  };
  //loadData();
};

const router = useRouter();
/**
 * to /view/question
 * @param question
 */
const toQuestionPage = (question: Question) => {
  console.log("Navigating to update page with ID:", question.id);
  router.push({
    path: `/view/question/${question.id}`,
  });
};

const show = ref(true);
const dataList = ref([]);
const total = ref(0);
const searchParameters = ref({
  title: "",
  tags: [],
  pageSize: 2,
  current: 1,
});
const onPageChange = (newPageNum: number) => {
  searchParameters.value = {
    ...searchParameters.value,
    current: newPageNum,
  };
};
const onSizeChange = (newSize: number) => {
  searchParameters.value = {
    ...searchParameters.value,
    pageSize: newSize,
  };
};
const loadData = async () => {
  const result = await QuestionControllerService.listQuestionVoByPageUsingPost(
    searchParameters.value
  );
  if (result.code === 0) {
    dataList.value = result.data.records;
    total.value = result.data.total;
  } else {
    message.error("Failed" + result.message);
  }
};
watchEffect(() => {
  loadData();
});

onMounted(() => {
  loadData();
});

const columns = [
  {
    title: "No.",
    dataIndex: "id",
  },
  {
    title: "Title",
    dataIndex: "title",
  },
  {
    title: "Tags",
    slotName: "tags",
  },
  {
    title: "AcceptanceRate",
    slotName: "acceptanceRate",
  },
  /* {
     title: "SubmitNum",
     dataIndex: "submitNum",
   },
   {
     title: "AcceptNum",
     dataIndex: "acceptNum",
   },*/
  {
    title: "CreateTime",
    slotName: "createTime",
  },
  {
    slotName: "optional",
  },
];
</script>

<style scoped>
#questionsView {
  max-width: 1280px;
  margin: 0 auto;
}
</style>
