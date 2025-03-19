<template>
  <div id="submittedQuestionsView">
    <h2>Manage Question</h2>
    <a-space direction="vertical" size="large" :style="{ width: '500px' }">
      <a-form :model="searchParameters" layout="vertical">
        <a-form-item field="language" label="Language">
          <a-select
            v-model="searchParameters.language"
            :style="{ width: '320px' }"
            placeholder="Please select a coding language"
          >
            <a-option>java</a-option>
            <a-option>cpp</a-option>
            <a-option>go</a-option>
            <a-option>html</a-option>
          </a-select>
        </a-form-item>
        <a-form-item
          field="questionId"
          lable="Question Id"
          style="width: 320px"
        >
          <a-input-number
            v-model="searchParameters.questionId"
            placeholder="Question Id"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="doSubmit">Search</a-button>
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
      <template #judgeInfo="{ record }">
        JudgeInfo {{ JSON.stringify(record.judgeInfo) }}
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("MMM Do YYYY") }}
      </template>
      <template #status="{ record }">
        <a-tag v-if="record.status === 2" color="green">Success</a-tag>
        <a-tag v-else-if="record.status === 1" color="blue">Judging</a-tag>
        <a-tag v-else-if="record.status === 0" color="orange">Pending</a-tag>
        <a-tag v-else color="red">Failed</a-tag>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionSubmitQueryRequest,
} from "../../../generated";
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
const searchParameters = ref<QuestionSubmitQueryRequest>({
  questionId: undefined,
  language: undefined,
  pageSize: 2,
  current: 1,
});
//questionId: number to string
const questionIdString = computed({
  get() {
    return searchParameters.value.questionId?.toString() || "";
  },
  set(newValue: string) {
    searchParameters.value.questionId = newValue ? Number(newValue) : undefined;
  },
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
  const result =
    await QuestionControllerService.listQuestionSubmitByPageUsingPost({
      ...searchParameters.value,
      sortField: "createTime",
      sortOrder: "descend",
    });
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
    title: "Id",
    dataIndex: "id",
  },
  {
    title: "Language",
    dataIndex: "language",
  },
  {
    title: "JudgeInfo",
    slotName: "judgeInfo",
  },
  {
    title: "Status",
    //status: 0-Pending, 1-Judging, 2-Success, 3-Failure
    slotName: "status",
  },
  {
    title: "QuestionId",
    dataIndex: "questionId",
  },
  {
    title: "UserId",
    dataIndex: "userId",
  },
  {
    title: "CreateTime",
    slotName: "createTime",
  },
];
</script>

<style scoped>
#submittedQuestionsView {
  max-width: 1280px;
  margin: 0 auto;
}
</style>
