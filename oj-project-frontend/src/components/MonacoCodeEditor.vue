<template>
  <div
    id="code-editor"
    ref="codeEditorRef"
    style="width: 99%; min-height: 400px; height: 55vh"
  />
  <!--  <a-button @click="fillValue">FillValues</a-button>-->
</template>
<script setup lang="ts">
import * as monaco from "monaco-editor";
import { defineProps, onMounted, ref, toRaw, watch, withDefaults } from "vue";

/**
 * Define component property types
 */
interface Props {
  value: string;
  handleChange: (v: string) => void;
  language?: string;
}

/**
 * Define variables and assign initial values to the component.
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  handleChange: (v: string) => {
    console.log(v);
  },
  language: () => "java",
});
watch(
  () => props.language,
  () => {
    if (editorInstance.value) {
      monaco.editor.setModelLanguage(
        toRaw(editorInstance.value).getModel(),
        props.language
      );
    }
  }
);
/*watch(
  () => props.language,
  () => {
    alert(props.language);
    editorInstance.value = monaco.editor.create(codeEditorRef.value, {
      value: props.value,
      language: props.language,
      automaticLayout: true,
      lineNumbers: "on",
      roundedSelection: false,
      scrollBeyondLastLine: false,
      readOnly: false,
      theme: "vs-dark",
      minimap: {
        enabled: false,
      },
    });
  }
);*/

const codeEditorRef = ref();
const editorInstance = ref();
const someValue = ref("Hello Kitty");
// change value
const fillValue = () => {
  if (!editorInstance.value) {
    return;
  }
  toRaw(editorInstance.value).setValue("newValue");
};

onMounted(() => {
  //if value is null
  if (!codeEditorRef.value) {
    return;
  }
  // Hover on each property to see its docs!
  editorInstance.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value,
    language: props.language,
    automaticLayout: true,
    lineNumbers: "on",
    roundedSelection: false,
    scrollBeyondLastLine: false,
    readOnly: false,
    theme: "vs-dark",
    minimap: {
      enabled: false,
    },
  });
  //Edit: Listen for content changes
  editorInstance.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(editorInstance.value).getValue());
  });
});
</script>

<style scoped></style>
