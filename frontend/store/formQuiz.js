export const state = () => ({
  quiz: emptyQuiz()
})

export const actions = {
  initQuiz(context) {
    context.commit('select', emptyQuiz())
  },
  addQuestion(context, question) {
    if (!question) {
      question = emptyQuestion()
    }
    context.commit('addQuestion', question)
  },
  add(context, quiz) {
    this.$axios.$post('http://localhost:8080/quizzes', quiz).then((quiz) => {
      context.commit('select', quiz)
      context.dispatch('all')
    })
  }
}

export const mutations = {
  select(state, quiz) {
    state.quiz = quiz
  },
  addQuestion(state, question) {
    if (!state.quiz.questions) {
      state.quiz.questions = []
    }
    state.quiz.questions.push(question)
  }
}

function emptyQuestion() {
  return {
    question: 'Write your question'
  }
}

function emptyQuiz() {
  return {
    questions: [emptyQuestion()]
  }
}
