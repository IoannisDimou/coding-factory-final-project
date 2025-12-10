const Footer = () => {
    const currentYear = new Date().getFullYear()

    return (
        <footer className="bg-ws-dark text-white mt-auto">
            <div className="container mx-auto px-4 py-6 text-center text-sm">
                @{currentYear} Arctic Builds. All rights reserved.
            </div>
        </footer>
    )
}

export default Footer
