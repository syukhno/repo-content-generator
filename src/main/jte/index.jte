@import gg.jte.Content

@template.layout.page(
title = "Repository Content Generator",
content = @`
    <div class="text-center mb-12">
        <h1 class="text-4xl font-bold tracking-tight text-gray-900 mb-2">Repository Content Generator</h1>
        <p class="text-gray-600">Generate a consolidated view of your repository contents</p>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
        <form hx-post="/generate" hx-target="#result-container" hx-indicator="#spinner">
            <div class="mb-6">
                <label for="githubUrl" class="block text-sm font-medium text-gray-700 mb-2">Repository URL</label>
                <div class="mt-1 relative">
                    <input
                            type="text"
                            id="githubUrl"
                            name="githubUrl"
                            required
                            class="block w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                            placeholder="https://github.com/username/repo"
                    >
                </div>
            </div>

            <button
                    type="submit"
                    class="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-4 rounded-lg transition-colors duration-200 flex items-center justify-center"
            >
                Generate Content
            </button>
        </form>

        <div id="spinner" class="htmx-indicator flex justify-center mt-6">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>

        <div id="result-container" class="mt-6">
            <!-- Generated content will appear here -->
        </div>
    </div>
`
)