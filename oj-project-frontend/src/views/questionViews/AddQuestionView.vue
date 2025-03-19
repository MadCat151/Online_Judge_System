<template>
  <div id="addQuestionView">
    <h2>Add Question</h2>
  </div>
  <a-form :model="form">
    <!---------------title--------------->
    <a-form-item field="title" label="Title">
      <a-input
        v-model="form.title"
        placeholder="Please enter a title."
        :style="{ width: '800px' }"
      />
    </a-form-item>
    <!---------------tags--------------->
    <a-form-item field="tags" label="Tags">
      <a-input-tag
        v-model="form.tags"
        placeholder="Please Enter Tags."
        allow-clear
        :style="{ width: '800px' }"
      />
    </a-form-item>
    <!---------------answer--------------->
    <a-form-item field="answer" label="Answer">
      <MdEditor
        :value="form.answer"
        :handle-change="onAnswerChange"
        style="min-width: 800px"
      />
    </a-form-item>
    <!---------------content--------------->
    <a-form-item field="content" label="Content">
      <MdEditor
        :value="form.content"
        :handle-change="onContentChange"
        style="min-width: 800px"
      />
    </a-form-item>
    <!---------------JudgeCase--------------->
    <a-form-item
      label="JudgeCase"
      :content-flex="false"
      :merge-props="false"
      style="margin-top: 30px"
    >
      <a-form-item
        v-for="(j_case, index) of form.judgeCase"
        :key="index"
        no-style
      >
        <a-space direction="vertical" fill>
          <a-form-item
            :field="`form.judgeCase[${index}].input`"
            :label="`InputCase ${index + 1}`"
            :key="index"
          >
            <a-input
              v-model="j_case.input"
              placeholder="please enter input judge case"
              style="width: 450px"
            />
          </a-form-item>
          <a-form-item
            :field="`form.judgeCase[${index}].output`"
            :label="`OutputCase ${index + 1}`"
            :key="index"
          >
            <a-input
              v-model="j_case.output"
              placeholder="please enter output judge case"
              style="width: 450px"
            />
          </a-form-item>
          <!---------------Delete--------------->
          <a-form-item>
            <a-button
              @click="handleDelete(index)"
              :style="{ marginBottom: '5px', marginRight: '0px' }"
            >
              <template #icon>
                <icon-delete />
              </template>
              <template #default>Delete</template>
            </a-button>
          </a-form-item>
        </a-space>
      </a-form-item>
      <!---------------Add JudgeCase--------------->
      <div style="margin-top: 16px">
        <a-button @click="handleAdd" type="dashed">
          <template #icon>
            <icon-plus />
          </template>
          <template #default>Add JudgeCase</template>
        </a-button>
      </div>
    </a-form-item>
    <!---------------JudgeConfig--------------->
    <a-form-item
      label="JudgeConfig"
      :content-flex="false"
      :merge-props="false"
      style="margin-top: 30px"
    >
      <a-space direction="vertical" fill>
        <a-form-item field="judgeConfig.memoryLimit" label="Memory Limit">
          <a-input-number
            v-model="form.judgeConfig.memoryLimit"
            placeholder="Please enter memory limit."
            mode="button"
            size="large"
            :style="{ width: '320px' }"
            :min="0"
            :max="10000"
          />
        </a-form-item>
        <a-form-item field="judgeConfig.stackLimit" label="Stack Limit">
          <a-input-number
            v-model="form.judgeConfig.stackLimit"
            placeholder="Please enter stack limit."
            mode="button"
            aria-valuemin="0"
            aria-valuemax="100"
            size="large"
            :style="{ width: '320px' }"
            :min="0"
            :max="10000"
          />
        </a-form-item>
        <a-form-item field="judgeConfig.timeLimit" label="Time Limit">
          <a-input-number
            v-model="form.judgeConfig.timeLimit"
            placeholder="Please enter time limit."
            mode="button"
            aria-valuemin="0"
            aria-valuemax="100"
            size="large"
            :style="{ width: '320px' }"
            :min="0"
            :max="10000"
          />
        </a-form-item>
      </a-space>
    </a-form-item>
    <!---------------Submit--------------->
    <a-form-item>
      <a-button type="primary" style="min-width: 200px" @click="doSubmit"
        >Submit
      </a-button>
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import { onMounted, reactive } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { IconDelete, IconPlus } from "@arco-design/web-vue/es/icon";
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRoute } from "vue-router";

const route = useRoute();
const updatePage = route.path.includes("update");

const form = reactive({
  answer: "",
  content: "",
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
  judgeConfig: {
    memoryLimit: 1000,
    stackLimit: 1000,
    timeLimit: 1000,
  },
  tags: [],
  title: "",
});
const loadData = async () => {
  const id = route.query.id;
  console.log("Retrieved ID from route:", id);
  if (!id || Array.isArray(id)) {
    return;
  }

  const result = await QuestionControllerService.getQuestionByIdUsingGet(
    id as any
  );
  if (result.code === 0) {
    Object.assign(form, result.data);
    if (!form.judgeCase) {
      form.judgeCase = [
        {
          input: "",
          output: "",
        },
      ];
    } else {
      form.judgeCase = JSON.parse(form.judgeCase as any);
    }
    if (!form.judgeConfig) {
      form.judgeConfig = {
        memoryLimit: 1000,
        stackLimit: 1000,
        timeLimit: 1000,
      };
    } else {
      form.judgeConfig = JSON.parse(form.judgeConfig as any);
    }
    if (!form.tags) {
      form.tags = [];
    } else {
      form.tags = JSON.parse(form.tags as any);
    }
  } else {
    message.error("FAILED" + result.message);
  }
};

onMounted(() => {
  loadData();
});

const onAnswerChange = (value: string) => {
  form.answer = value;
};

const onContentChange = (value: string) => {
  form.content = value;
};

const doSubmit = async () => {
  console.log(form);
  if (updatePage) {
    const res = await QuestionControllerService.updateQuestionUsingPost(form);
    if (res.code === 0) {
      message.success("UPDATE: SUCCESS");
    } else {
      message.error("UPDATE: Failed" + res.message);
    }
  } else {
    const res = await QuestionControllerService.addQuestionUsingPost(form);
    if (res.code === 0) {
      message.success("ADD: SUCCESS");
    } else {
      message.error("ADD: Failed" + res.message);
    }
  }
};

const handleAdd = () => {
  form.judgeCase.push({
    input: "",
    output: "",
  });
};
const handleDelete = (index: number) => {
  form.judgeCase.splice(index, 1);
};
</script>

<style scoped>
#addQuestionView {
}

#addQuestionView {
  display: flex;
  justify-content: center; /* Centers content horizontally */
  align-items: center; /* Centers content vertically */
  height: 100px; /* Adjust the height based on your needs */
  margin: 0 auto; /* Horizontally centers the entire div */
  text-align: center; /* Ensures the text inside is horizontally centered */
  border: 0px solid #ccc; /* Optional: adds a border to visualize the div size */
}
</style>
