<template>
  <div id="manageQuestionView">
    <h2>Manage Question</h2>
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
      @page-size-change="onPageSizeChange"
    >
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="doUpdate(record)">update</a-button>
          <a-button status="danger" @click="doDelete(record)">delete</a-button>
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

const router = useRouter();
const doUpdate = (question: Question) => {
  console.log("Navigating to update page with ID:", question.id);
  router.push({
    path: "/update/question",
    query: {
      id: question.id,
    },
  });
};

const doDelete = async (question: Question) => {
  console.log(question);
  const result = await QuestionControllerService.deleteQuestionUsingPost({
    id: question.id,
  });
  if (result.code === 0) {
    message.success("SUCCESS");
    loadData();
  } else {
    message.success("FAILED");
  }
};
const show = ref(true);
const dataList = ref([]);
const total = ref(0);
const searchParameters = ref({
  pageSize: 2,
  current: 1,
  sortField: "CreateTime",
  sortOrder: "desc",
});
const onPageChange = (newPageNum: number) => {
  searchParameters.value = {
    ...searchParameters.value,
    current: newPageNum,
  };
};
const onPageSizeChange = (newPageSize: number) => {
  console.log("Page size changed to:", newPageSize);
  searchParameters.value = {
    ...searchParameters.value,
    pageSize: newPageSize,
  };
};
const loadData = async () => {
  const result = await QuestionControllerService.listQuestionByPageUsingPost(
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
    title: "id",
    dataIndex: "id",
  },
  {
    title: "Title",
    dataIndex: "title",
  },
  {
    title: "Content",
    dataIndex: "content",
  },
  {
    title: "Tags",
    dataIndex: "tags",
  },
  {
    title: "Answer",
    dataIndex: "answer",
  },
  {
    title: "SubmitNum",
    dataIndex: "submitNum",
  },
  {
    title: "AcceptNum",
    dataIndex: "acceptNum",
  },
  {
    title: "JudgeConfig",
    dataIndex: "judgeConfig",
  },
  {
    title: "JudgeCase",
    dataIndex: "judgeCase",
  },
  {
    title: "UserId",
    dataIndex: "userId",
  },
  {
    title: "CreateTime",
    dataIndex: "createTime",
  },
  {
    title: "Optional",
    slotName: "optional",
  },
];
</script>

<style scoped></style>
