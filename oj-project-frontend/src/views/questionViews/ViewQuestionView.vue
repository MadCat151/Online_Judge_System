<template>
  <div id="viewQuestionsView">
    <a-row :gutter="[24, 24]">
      <a-col :md="12" :xs="24">
        <a-tabs default-active-key="question">
          <a-tab-pane key="question" title="Question">
            <a-card v-if="questionVO" :title="questionVO.title">
              <a-descriptions
                title="Judge Configs"
                :column="{ xs: 1, md: 2, lg: 3 }"
              >
                <a-descriptions-item label="time limit">
                  {{ questionVO.judgeConfig?.timeLimit ?? 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="memory limit">
                  {{ questionVO.judgeConfig?.memoryLimit ?? 0 }}
                </a-descriptions-item>
              </a-descriptions>
              <MdViewer :value="questionVO?.content" />
              <template #extra>
                <a-space wrap>
                  <a-tag
                    v-for="(tag, index) of questionVO.tags"
                    :key="index"
                    color="green"
                    >{{ tag }}
                  </a-tag>
                </a-space>
              </template>
            </a-card>
          </a-tab-pane>
          <a-tab-pane key="comment" title="Comment">
            Content of Tab Panel 2
          </a-tab-pane>
          <a-tab-pane key="createTime" title="CreateTime">
            <template #title>Sample Answer</template>
            <!--            {{ questionVO?.createTime }}-->
            <pre>
              public class Main
              {
                  public static void main(String[] args) {
                      int a = Integer.parseInt(args[0]);
                      int b = Integer.parseInt(args[1]);
                      System.out.println(a + b);
                  }
              }
            </pre>
          </a-tab-pane>
        </a-tabs>
      </a-col>
      <a-col :md="12" :xs="24">
        <a-space
          direction="horizontal"
          size="large"
          :style="{ width: '500px' }"
        >
          <a-form :model="form" layout="vertical">
            <a-form-item field="title" label="Language">
              <a-select
                v-model="form.language"
                :style="{ width: '320px' }"
                placeholder="Please select a coding language"
              >
                <a-option>java</a-option>
                <a-option>cpp</a-option>
                <a-option>go</a-option>
                <a-option>html</a-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-space>
        <!--        {{ form.language }}-->
        <MonacoCodeEditor
          :value="form.code as string"
          :language="form.language"
          :handle-change="changeCode"
        />
        <a-divider style="border-width: 0"></a-divider>
        <a-button type="primary" style="min-width: 170px" @click="doSubmit"
          >Submit
        </a-button>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, ref, withDefaults } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionSubmitAddRequest,
  QuestionVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import MonacoCodeEditor from "@/components/MonacoCodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";

const question = ref<Question>({
  answer: "Sample Answer",
});

const changeCode = (value: string) => {
  form.value.code = value;
};

const form = ref<QuestionSubmitAddRequest>({
  language: "java",
  code: "",
});
const doSubmit = async () => {
  if (!questionVO.value?.id) {
    return;
  }
  const result = await QuestionControllerService.doQuestionSubmitUsingPost({
    ...form.value,
    questionId: questionVO.value?.id,
  });
  if (result.code === 0) {
    message.success("SUCCESS");
  } else {
    message.error("FAILED" + result.message);
  }
};

interface Props {
  id: string;
}

const object = withDefaults(defineProps<Props>(), {
  id: () => "",
});
const questionVO = ref<QuestionVO>();

const loadData = async () => {
  const result = await QuestionControllerService.getQuestionVoByIdUsingGet(
    object.id as any
  );
  if (result.code === 0) {
    questionVO.value = result.data;
  } else {
    message.error("Failed" + result.message);
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
#viewQuestionsView {
  max-width: 1400px;
  margin: 0 auto;
}

#viewQuestionsView >>> .arco-space-item {
  margin-bottom: 0 !important;
}
</style>
