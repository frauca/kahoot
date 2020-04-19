export const state = () => ({
  quiz: emptyQuiz()
})

export const actions = {
  initQuiz(context) {
    context.commit('select', emptyQuiz())
  },
  add(context, quiz) {
    this.$axios.$post('http://localhost:8080/quizzes', quiz).then((quiz) => {
      context.commit('select', quiz)
      context.dispatch('all')
    })
  },
  select(context, quiz) {
    if (!quiz) {
      quiz = emptyQuiz()
    }
    context.commit('select', quiz)
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

function emptyAnswer() {
  return {
    answer: 'Write your 1st answer',
    correct: false
  }
}

function emptyQuestion() {
  return {
    question: 'Write your 1st question',
    answers: [emptyAnswer()]
  }
}

function emptyQuiz() {
  return {
    questions: [emptyQuestion()]
  }
}
