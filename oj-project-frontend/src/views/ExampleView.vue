<template>
  <div id="exampleView">
    <MdEditor :value="MdEditorValue" :handle-change="onMdChange" />
    <MonacoCodeEditor
      :value="MonacoCodeValue"
      :handle-change="onMonacoCodeChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import MonacoCodeEditor from "@/components/MonacoCodeEditor.vue"; // @ is an alias to /src

//Markdown
const MdEditorValue = ref();

const onMdChange = (v: string) => {
  MdEditorValue.value = v;
};

//Monaco CodeEditor
const MonacoCodeValue = ref();

const onMonacoCodeChange = (v: string) => {
  MonacoCodeValue.value = v;
};
</script>
